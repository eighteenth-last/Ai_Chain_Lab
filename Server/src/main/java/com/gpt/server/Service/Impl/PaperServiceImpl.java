package com.gpt.server.Service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpt.server.Entity.ExamRecord;
import com.gpt.server.Entity.Paper;
import com.gpt.server.Entity.PaperQuestion;
import com.gpt.server.Entity.Question;
import com.gpt.server.Mapper.ExamRecordMapper;
import com.gpt.server.Mapper.PaperMapper;
import com.gpt.server.Mapper.QuestionMapper;
import com.gpt.server.Service.PaperQuestionService;
import com.gpt.server.Service.PaperService;
import com.gpt.server.Vo.AiPaperVo;
import com.gpt.server.Vo.PaperVo;
import com.gpt.server.Vo.RuleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service.Impl
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Paper服务实现类
 * @Version: 1.0
 */
@Slf4j
@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    @Autowired
    private PaperQuestionService paperQuestionService;

    @Autowired
    private ExamRecordMapper examRecordMapper;

    @Autowired
    private QuestionMapper questionMapper;

    // 手动创建试卷
    @Transactional
    @Override
    public Paper createPaper(PaperVo paperVo) {
        // 1、完善试卷信息
        Paper paper = new Paper();
        BeanUtils.copyProperties(paperVo, paper);
        paper.setStatus("DRAFT"); // 设置试卷状态为草稿
            // 检查是否传入题目信息
        if(ObjectUtils.isEmpty(paperVo.getQuestions())){
            paper.setTotalScore(BigDecimal.ZERO);
            paper.setQuestionCount(0);
            // 保存题目信息对象
            save(paper);
            log.warn("当前试卷：{}没有题目，只能用于试卷编辑，不能用于考试", paper);
            return paper;
        }
        // 有题目根据题目数据计算总分数
        // 题目数量
        paper.setQuestionCount(paperVo.getQuestions().size());
        // 总分数
        Optional<BigDecimal> totalScore = paperVo.getQuestions().values().stream().reduce(BigDecimal::add);
        paper.setTotalScore(totalScore.get());
        // 2、保存试卷
        log.debug("当前试卷勾选了题目信息，正常进行计算和保存，试卷信息为：{}",paper);
        save(paper);
        // 3、判断试卷是否携带了题目集合，携带了，后续进行数据的中间表处理
        // 4、试卷集合的map->数据题目中间表对象集合
        List<PaperQuestion> paperQuestionList =paperVo.getQuestions().entrySet().stream().map(entry ->
                new PaperQuestion(paper.getId().intValue(), Long.valueOf(entry.getKey()), entry.getValue())).toList();
        // 5、试卷题目中间表的业务批量插入方法完成批量插入
        paperQuestionService.saveBatch(paperQuestionList);
        // 6、返回保存的试卷对象
        return paper;
    }

    // AI创建试卷
    @Override
    public Paper createPaperWithAI(AiPaperVo aiPaperVo) {
        Paper paper=new Paper();
        BeanUtils.copyProperties(aiPaperVo, paper);
        paper.setStatus("DRAFT");
        save(paper);

        // 循环每个规则
        int questionCount=0;
        BigDecimal totalScore=BigDecimal.ZERO;
        for(RuleVo rule:aiPaperVo.getRules()){
            if (rule.getCount()==0){
                log.debug("{}类别规则下，要求题目数量为0，直接跳出",rule.getType().name());
                continue;
            }
            LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Question::getType,rule.getType().name());
            queryWrapper.in(!ObjectUtils.isEmpty(rule.getCategoryIds()),Question::getCategoryId,rule.getCategoryIds());
            List<Question> questionAllList = questionMapper.selectList(queryWrapper);
            if (ObjectUtils.isEmpty(questionAllList)){
                log.debug("{}类别规则下，没有找到题目",rule.getType().name());
                continue;
            }
            // 校验满足规则题目的需要的数量对比,谁小要谁
            int ruleNumber = Math.min(rule.getCount(), questionAllList.size());

            questionCount+=ruleNumber;  // 题目数量累加
            totalScore = totalScore.add(BigDecimal.valueOf((long) ruleNumber * rule.getScore()));

            // 随机选出符合题目的集合
            Collections.shuffle(questionAllList);
            List<Question> questionList = questionAllList.subList(0, ruleNumber);

            // 题目集合转成对象集合
            List<PaperQuestion> paperQuestionList = questionList.stream().map(q ->
                    new PaperQuestion(paper.getId().intValue(), q.getId(), BigDecimal.valueOf(rule.getScore())))
                    .toList();
            paperQuestionService.saveBatch(paperQuestionList);// 批量保存
        }
        paper.setTotalScore(totalScore);
        paper.setQuestionCount(questionCount);

        updateById(paper);
        return paper;
    }

    // 更新试卷
    @Override
    public Paper updataPaper(Integer id, PaperVo paperVo) {
        // 校验,原试卷对象
        Paper paper = getById(id);
        if("PUBLISHED".equals(paper.getStatus())){
            throw new RuntimeException("当前试卷已发布，不能进行更新");
        }
        LambdaQueryWrapper<Paper> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Paper::getId, id);
        queryWrapper.eq(Paper::getName, paperVo.getName());
        long count = count(queryWrapper);
        if(count>0){
            throw new RuntimeException("当前试卷名称已存在，更新失败");
        }

        BeanUtils.copyProperties(paperVo, paper);
        Optional<BigDecimal> totalScore = paperVo.getQuestions().values().stream().reduce(BigDecimal::add);
        paper.setTotalScore(totalScore.get());
        updateById(paper);
        LambdaQueryWrapper<PaperQuestion> paperQuestionLambdaQueryWrapper=new LambdaQueryWrapper<>();
        paperQuestionService.remove(paperQuestionLambdaQueryWrapper);
        // 插入
        List<PaperQuestion> paperQuestionList =paperVo.getQuestions().entrySet().stream().map(entry ->
                new PaperQuestion(paper.getId().intValue(), Long.valueOf(entry.getKey()), entry.getValue())).toList();
        paperQuestionService.saveBatch(paperQuestionList);
        log.debug("更新试卷成功，更新数据为：{}", paper);
        return paper;
    }

    // 删除试卷
    @Override
    public void removePaper(Integer id) {
        Paper paper = getById(id);
        if("PUBLISHED".equals(paper.getStatus())){
            throw new RuntimeException("当前试卷已发布，不能进行删除");
        }
        LambdaQueryWrapper<ExamRecord> examRecordLambdaQueryWrapper = new LambdaQueryWrapper<>();
        examRecordLambdaQueryWrapper.eq(ExamRecord::getExamId, id);
        long count = examRecordMapper.selectCount(examRecordLambdaQueryWrapper);
        if(count>0){
            throw new RuntimeException("当前试卷已关联考试记录，不能进行删除");
        }
        removeById(id);
        log.debug("删除试卷成功，删除数据为：{}", paper);
        LambdaQueryWrapper<PaperQuestion> paperQuestionLambdaQueryWrapper=new LambdaQueryWrapper<>();
        paperQuestionLambdaQueryWrapper.eq(PaperQuestion::getPaperId, id);
        paperQuestionService.remove(paperQuestionLambdaQueryWrapper);
    }

    // 根据id查询试卷
    @Override
    public Paper getPaperById(Integer id) {
        if (id == null) {
            log.error("根据id查询试卷失败，id为空");
            throw new RuntimeException("试卷ID不能为空");
        }
        Paper paper = getById(id);
        if (paper == null) {
            log.warn("根据id查询试卷失败，未找到id为{}的试卷", id);
            return null;
        }
        List<Question> questionList = questionMapper.selectQuestionByPaperId(Long.valueOf(id));
        // 校验题目数据是否为空
        if(ObjectUtils.isEmpty(questionList)){
            log.warn("查询试卷{}下的题目数据为空",id);
            return paper;
        }
        questionList.sort((q1,q2) -> Integer.compare(typeToInt(q1.getType()),typeToInt(q2.getType())));
        paper.setQuestions(questionList);
        return paper;
    }
    private int typeToInt(String type) {
        switch (type){
            case "CHOICE":
                return 1;
            case "TEXT":
                return 2;
            case "JUDGE":
                return 3;
            default:
                return 0;
        }
    }
}
