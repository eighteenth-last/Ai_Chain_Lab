package com.gpt.server.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpt.server.Common.CacheConstants;
import com.gpt.server.Entity.Question;
import com.gpt.server.Entity.QuestionAnswer;
import com.gpt.server.Entity.QuestionChoice;
import com.gpt.server.Mapper.QuestionAnswerMapper;
import com.gpt.server.Mapper.QuestionChoiceMapper;
import com.gpt.server.Mapper.QuestionMapper;
import com.gpt.server.Service.QuestionService;
import com.gpt.server.Utils.RedisUtils;
import com.gpt.server.Vo.QuestionQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service.Impl
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Question服务实现类
 * @Version: 1.0
 */
@Slf4j
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionChoiceMapper questionChoiceMapper;
    
    @Autowired
    private QuestionAnswerMapper questionAnswerMapper;

    @Autowired
    private RedisUtils redisUtils;
    
    // 查询题目列表分页，分步查询
    @Override
    public void queryQuestionListPage(Page<Question> questionPage, QuestionQueryVo questionQueryVo) {
        questionMapper.selectQuestionPage(questionPage, questionQueryVo);
    }

    // java代码处理
    @Override
    public void queryQuestionListByStream(Page<Question> questionPage, QuestionQueryVo questionQueryVo) {
        // 题目单表分页+动态条件查询
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(questionQueryVo.getCategoryId() != null, Question::getCategoryId, questionQueryVo.getCategoryId());
        queryWrapper.eq(!ObjectUtils.isEmpty(questionQueryVo.getDifficulty()), Question::getDifficulty, questionQueryVo.getDifficulty());
        queryWrapper.eq(!ObjectUtils.isEmpty(questionQueryVo.getType()), Question::getType, questionQueryVo.getType());
        queryWrapper.like(!ObjectUtils.isEmpty(questionQueryVo.getKeyword()), Question::getTitle, questionQueryVo.getKeyword());
        queryWrapper.orderByDesc(Question::getCreateTime);
        page(questionPage,queryWrapper);
        
        // 判断，如果没有满足条件的题目信息
        if (ObjectUtils.isEmpty(questionPage.getRecords())){
            log.debug("没有符合条件的题目信息，后续已停止");
            return;
        }
        // 查询题目对应的所有选项和所有答案
        List<Long> questionIds = questionPage.getRecords().stream().map(Question::getId).toList();
        // 根据题目ID查询所有选项
        LambdaQueryWrapper<QuestionChoice> questionChoiceQueryWrapper = new LambdaQueryWrapper<>();
        questionChoiceQueryWrapper.in(QuestionChoice::getQuestionId, questionIds);
        List<QuestionChoice> questionChoices=questionChoiceMapper.selectList(questionChoiceQueryWrapper);
        
        LambdaQueryWrapper<QuestionAnswer> questionAnswerQueryWrapper = new LambdaQueryWrapper<>();
        questionAnswerQueryWrapper.in(QuestionAnswer::getQuestionId, questionIds);
        List<QuestionAnswer> questionAnswers=questionAnswerMapper.selectList(questionAnswerQueryWrapper);
        // 题目的选项和答案转成map格式
        Map<Long, QuestionAnswer> qusentAnswerMap = questionAnswers.stream().collect(Collectors.toMap(QuestionAnswer::getQuestionId, a -> a));
        Map<Long, List<QuestionChoice>> questionChoiceMap = questionChoices.stream().collect(Collectors.groupingBy(QuestionChoice::getQuestionId));
        
        // 循环题目表，进行题目的选项和方案的赋值工作
        questionPage.getRecords().forEach(question -> { 
            // 给题目答案赋值
            question.setAnswer(qusentAnswerMap.get(question.getId()));
            // 给题目选项赋值（只有选择题有选项）
            if("CHOICE".equals(question.getType())){
                // 只要是选项的操作，靠排律排序的问题
                List<QuestionChoice> questionChoicesSort = questionChoiceMap.get(question.getId());
                // 字段排序，从小到大倒序
                if (!ObjectUtils.isEmpty(questionChoicesSort)){
                    questionChoicesSort.sort(Comparator.comparing(QuestionChoice::getSort));
                    question.setChoices(questionChoicesSort);
                }
            }
        });
    }

    // 查询题目详情

    /**
     * 查询 题目+答案+选项
     * 嵌套结果 连表查询 +result[可以使用，没有分页]
     * 嵌套查询 分布查询
     * 查询+java代码赋值
     * @param id
     * @return
     */
    @Override
    public Question queryQuestionById(Long id) {
        // 查询题目详情对象
        Question question = getById(id);
        if (question== null){
            log.info("没有查询到题目信息，id：{}", id);
            throw new RuntimeException("查询id为%s的题目已不存在".formatted(id));
        }
        // 查询题目对应的答案
        QuestionAnswer questionAnswer = questionAnswerMapper.selectOne(new LambdaQueryWrapper<QuestionAnswer>().eq(QuestionAnswer::getQuestionId, id));
        question.setAnswer(questionAnswer);
        // 查询题目对应的答案
        if("CHOICE".equals(question.getType())){
            List<QuestionChoice> questionChoices = questionChoiceMapper.selectList(new LambdaQueryWrapper<QuestionChoice>().eq(QuestionChoice::getQuestionId, id));
            question.setChoices(questionChoices);
        }
        // 题目赋值

        // 预留：进行redis的数据缓存Zset
        new Thread(()-> incrementQuestionScore(question.getId())).start();  // 创建一个子线程，进行redis的数据缓存

        return question;
    }

    // 保存题目
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveQuestion(Question question) {
        // 判断-题目不能重复
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getType,question.getType());
        queryWrapper.eq(Question::getTitle,question.getTitle());
        long count = count(queryWrapper);
        if (count > 0){
            throw new RuntimeException("该题目在%s类型下已存在名为%s的题目信息".formatted(question.getType(),question.getTitle()));
        }
        // 保存题目信息
        save(question);// mybatis-plus提供，自动主键回显

        // 判断是不是选择题，是选择题根据选项的正确答案赋值，同时将选项插入选项表
        QuestionAnswer answer = question.getAnswer();
        answer.setQuestionId(question.getId());
        if ("CHOICE".equals(question.getType())){
            List<QuestionChoice> questionChoices = question.getChoices();
            StringBuilder stringBuilder=new StringBuilder(); // 拼接正确答案
            for (int i = 0; i < questionChoices.size(); i++){
                QuestionChoice questionChoice = questionChoices.get(i);
                questionChoice.setSort(i);
                questionChoice.setQuestionId(question.getId());

                questionChoiceMapper.insert(questionChoice);
                if(questionChoice.getIsCorrect()){
                    if (!stringBuilder.isEmpty()){
                        stringBuilder.append(",");
                    }
                    stringBuilder.append((char)('A'+i));
                }
                answer.setAnswer(stringBuilder.toString());
            }
        }
        // 完成答案的插入
        questionAnswerMapper.insert(answer);
    }

    // 异步方法，在题目
    private void incrementQuestionScore(Long questionId){
        Double score = redisUtils.zIncrementScore(CacheConstants.POPULAR_QUESTIONS_KEY, questionId, 1);
        log.info("题目id：{}，当前分数：{}", questionId, score);
    }
}