package com.gpt.server.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpt.server.Entity.Question;
import com.gpt.server.Entity.QuestionAnswer;
import com.gpt.server.Entity.QuestionChoice;
import com.gpt.server.Mapper.QuestionAnswerMapper;
import com.gpt.server.Mapper.QuestionChoiceMapper;
import com.gpt.server.Mapper.QuestionMapper;
import com.gpt.server.Service.QuestionService;
import com.gpt.server.Vo.QuestionQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
}