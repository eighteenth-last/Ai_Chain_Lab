package com.gpt.server.Service;

import com.gpt.server.Entity.Question;
import com.gpt.server.Vo.AiGenerateRequestVo;
import com.gpt.server.Vo.GradingResult;
import com.gpt.server.Vo.QuestionImportVo;

import java.util.List;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: KimiAi服务接口
 * @Version: 1.0
 */
public interface DeepSeekAiService {
    /**
     * 生成ai评语
     * @param totalScore
     * @param maxScore
     * @param questionCount
     * @param correctCount
     * @return
     */
    String buildSummary(Integer totalScore, Integer maxScore, Integer questionCount, Integer correctCount) throws InterruptedException;


    /**
     * 使用ai,进行简答题判断
     * @param question
     * @param userAnswer
     * @param maxScore
     * @return
     */
    GradingResult gradingTextQuestion(Question question, String userAnswer, Integer maxScore) throws InterruptedException;


    /**
     * 根据前台传递的上下文环境，生成对应的提示词
     * @param request
     * @return
     */
    String buildPrompt(AiGenerateRequestVo request);

    /**
     * 封装调用kimi模型，最终返结果
     * @param prompt
     * @return 返回生成题目json 结果 / choices / message / content
     */
    String callDeepSeekAi(String prompt) throws InterruptedException;

    /**
     * ai题目信息生成
     * @param request
     * @return
     */
    List<QuestionImportVo> aiGenerateQuestions(AiGenerateRequestVo request) throws InterruptedException;
} 