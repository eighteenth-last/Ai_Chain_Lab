package com.gpt.server.Service.Impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpt.server.Entity.AnswerRecord;
import com.gpt.server.Entity.ExamRecord;
import com.gpt.server.Entity.Paper;
import com.gpt.server.Entity.Question;
import com.gpt.server.Mapper.AnswerRecordMapper;
import com.gpt.server.Mapper.ExamRecordMapper;
import com.gpt.server.Service.AnswerRecordService;
import com.gpt.server.Service.DeepSeekAiService;
import com.gpt.server.Service.ExamService;
import com.gpt.server.Service.PaperService;
import com.gpt.server.Vo.StartExamVo;
import com.gpt.server.Vo.SubmitAnswerVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service.Impl
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Exam服务实现类
 * @Version: 1.0
 */
@Service
@Slf4j
public class ExamServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamService {

    @Autowired
    private PaperService paperService;

    @Autowired
    private AnswerRecordMapper answerRecordMapper;

    @Autowired
    private AnswerRecordService answerRecordService;

    @Autowired
    private DeepSeekAiService deepSeekAiService;

    // 开始考试
    @Override
    public ExamRecord startExam(StartExamVo startExamVo) {
        // 检查当前考生是否正在进行考试
        LambdaQueryWrapper<ExamRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamRecord::getStudentName, startExamVo.getStudentName());
        queryWrapper.eq(ExamRecord::getExamId, startExamVo.getPaperId()); // 试卷id
        queryWrapper.eq(ExamRecord::getStatus, "进行中");
        ExamRecord examRecord = getOne(queryWrapper);
        if (examRecord != null) {
            log.error("考生：{}在试卷：{}中存在有考试记录，直接返回对应的考试记录信息：{}", startExamVo.getStudentName(), startExamVo.getPaperId(), examRecord);
            return examRecord;
        }

        // 不全考试记录对象（进行中、已完成、已批阅）
        examRecord = new ExamRecord();
        examRecord.setExamId(startExamVo.getPaperId());  //试卷id
        examRecord.setStudentName(startExamVo.getStudentName()); //考生姓名
        examRecord.setStatus("进行中");  // 考试状态
        examRecord.setStartTime(LocalDateTime.now());  // 考试开始时间
        examRecord.setWindowSwitches(0);
        // 进行考试记录对象保存
        save(examRecord);
        // 返回考试记录
        log.info("开始考试成功，返回考试记录：{}", examRecord);
        return examRecord;
    }

    // 根据id查询考试记录
    @Override
    public ExamRecord getExamRecordById(Integer id) {
        ExamRecord examRecord = getById(id);
        if (examRecord == null) {
            log.error("查询考试记录失败，未找到id为{}的记录", id);
            throw new RuntimeException("查询考试记录失败");
        }
        log.info("查询考试记录成功，考试记录为：{}", examRecord);
        Paper paperById = null;
        if (examRecord.getExamId() == null) {
            log.warn("考试记录id为{}的试卷id为空，可能试卷已被删除", id);
        } else {
            paperById = paperService.getPaperById(examRecord.getExamId());
            examRecord.setPaper(paperById);
        }

        LambdaQueryWrapper<AnswerRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AnswerRecord::getExamRecordId,id);
        List<AnswerRecord> answerRecords = answerRecordMapper.selectList(queryWrapper);
        if (!ObjectUtils.isEmpty(answerRecords)){
            if (paperById != null && !ObjectUtils.isEmpty(paperById.getQuestions())) {
                // 获取试卷中的题目id集合，按试卷中的题目顺序排序
                List<Long> questionIds = paperById.getQuestions().stream().map(Question::getId).toList();
                answerRecords.sort((a1,a2)->{
                    int a1Index = questionIds.indexOf(a1.getQuestionId().longValue());
                    int a2Index = questionIds.indexOf(a2.getQuestionId().longValue());
                    return Integer.compare(a1Index,a2Index);
                });
            }
            // 装到考试记录对象中
            examRecord.setAnswerRecords(answerRecords);
        }
        return examRecord;
    }

    // 提交考试记录并ai判卷
    @Override
    public void submitExam(Integer examRecordId, List<SubmitAnswerVo> answers) throws InterruptedException {
        if (examRecordId == null) {
            throw new RuntimeException("考试记录ID不能为空");
        }
        ExamRecord examRecord = getById(examRecordId);
        if (examRecord == null) {
            throw new RuntimeException("考试记录不存在");
        }
        if ("已批阅".equals(examRecord.getStatus())) {
            log.warn("考试记录id为{}已经批阅，忽略重复提交", examRecordId);
            return;
        }
        // 清理本次考试之前的答题记录，避免重复记录导致多次判卷
        LambdaQueryWrapper<AnswerRecord> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(AnswerRecord::getExamRecordId, examRecordId);
        answerRecordMapper.delete(deleteWrapper);

        if(!ObjectUtils.isEmpty(answers)){
            List<AnswerRecord> answerRecordList = answers.stream()
                    .map(vo -> new AnswerRecord(examRecordId, vo.getQuestionId(), vo.getUserAnswer()))
                    .toList();
            // 批量保存
            answerRecordService.saveBatch(answerRecordList);
        }
        // 修改考试记录
        examRecord.setEndTime(LocalDateTime.now());
        examRecord.setStatus("已完成");
        updateById(examRecord);

        // 完成判卷
        graderExam(examRecordId);
    }

    // ai智能判卷
    @Override
    public ExamRecord graderExam(Integer examRecordId) throws InterruptedException {
        // 获取学生的考试信息
        ExamRecord examRecordById = getExamRecordById(examRecordId);
        // 校验考试记录对应的试卷是否被删除
        Paper paper = examRecordById.getPaper();
        if (paper == null) {
            examRecordById.setStatus("已批阅");
            examRecordById.setAnswers("考试信息已经被删除，无法判卷"); //ai 判卷结果
            updateById(examRecordById);
            log.warn("id为{}考试信息已经被删除，无法判卷", examRecordId);
            return examRecordById;
        }

        // 校验是否答题完毕
        List<AnswerRecord> answerRecords = examRecordById.getAnswerRecords();
        if (ObjectUtils.isEmpty(answerRecords)){
            examRecordById.setStatus("已批阅");
            examRecordById.setAnswers("提交白卷，直接判0");
            examRecordById.setScore(0);
            updateById(examRecordById);
            log.warn("id为{}的考试记录未完成答题，请完成答题", examRecordId);
            return examRecordById;
        }
        // 声明两个变量，记录正确的题目数量及总分数
        int correctCount = 0;
        int totalScore = 0;
        // 将试卷中的question题目集合
        Map<Long, Question> questionMap = paper.getQuestions().stream().collect(Collectors.toMap(Question::getId, q -> q));
        // 逐一判题，获取题目的答案及分数
        for (AnswerRecord answerRecord : answerRecords) {
            // 获取试卷的答案及分数
            Question question = questionMap.get(answerRecord.getQuestionId().longValue());
            if(question == null) continue;
            String systemAnswer = question.getAnswer().getAnswer();
            String userAnswer = answerRecord.getUserAnswer();
            if("JUDGE".equals(question.getType())){
                userAnswer=judgeToTrueOrFalse(userAnswer);
            }
            try {
                //非简答题
                if (!"TEXT".equals(question.getType())){
                    if(userAnswer.equals(systemAnswer)){
                        answerRecord.setIsCorrect(1);
                        answerRecord.setScore(question.getPaperScore().intValue());
                    }else{
                        answerRecord.setIsCorrect(0);
                        answerRecord.setScore(0);
                    }
                }else{
                    // 简答题
                    // todo: ai判卷
                    String prompt = deepSeekAiService.buildGradingPrompt(question, userAnswer, question.getScore());
                    String result = deepSeekAiService.callDeepSeekAi(prompt);
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    // ai的分数
                    Integer aiScore = jsonObject.getInteger("score");
                    if (aiScore >= question.getScore()){
                        answerRecord.setScore(question.getPaperScore().intValue());
                        answerRecord.setIsCorrect(1);  // 全对
                        answerRecord.setAiCorrection(jsonObject.getString("feedback"));
                    }else if(aiScore>=0){
                        // 证明是完全错位
                        answerRecord.setScore(0);
                        answerRecord.setIsCorrect(0);
                        answerRecord.setAiCorrection(jsonObject.getString("reason"));
                    }else {
                        // 部分正确
                        answerRecord.setScore(aiScore);
                        answerRecord.setIsCorrect(2);
                        answerRecord.setAiCorrection(jsonObject.getString("reason"));
                    }
                }
            }catch (Exception e){
                answerRecord.setIsCorrect(0);
                answerRecord.setScore(0);
                log.error("id为{}的考试记录，第{}道题判题异常，异常信息为：{}", examRecordId, answerRecord.getQuestionId(), e.getMessage());
            }
            totalScore+=answerRecord.getScore();
            if(answerRecord.getIsCorrect() == 1){
                correctCount++;
            }
        }
        // 修改每一条学生的考试答题记录
        answerRecordService.updateBatchById(answerRecords);
        // 调用ai生成评阅结果
        // todo :
        String aiResultPrompt = deepSeekAiService.buildAiResult(totalScore, paper.getTotalScore().intValue(), paper.getQuestionCount(), correctCount);
        String calledDeepSeekAi = deepSeekAiService.callDeepSeekAi(aiResultPrompt);
        // 更新考试记录
        examRecordById.setScore(totalScore);
        examRecordById.setAnswers(calledDeepSeekAi);
        examRecordById.setStatus("已批阅");
        updateById(examRecordById);
        return examRecordById;
    }

    // 转化判断题
    private String judgeToTrueOrFalse(String userAnswer) {
        userAnswer = userAnswer.toUpperCase();
        switch (userAnswer) {
            case "T":
            case "正确":
            case "对":
                return "TRUE";
            case "F":
            case "错误":
            case "不对":
            case "FALSE":
                return "FALSE";
            default:
                return userAnswer;
        }
    }
}
