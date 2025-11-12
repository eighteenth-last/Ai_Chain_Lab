package com.gpt.server.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Entity
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: PaperQuestion实体类
 * @Version: 1.0
 */
@TableName(value ="paper_question")
@Data
@NoArgsConstructor
public class PaperQuestion extends BaseEntity {

    /**
     * 试卷ID
     */
    private Integer paperId; // 试卷ID

    /**
     * 题目ID
     */
    private Long questionId; // 题目ID

    /**
     * 题目分数
     */
    private BigDecimal score; // 题目分数


    /**
     * 构造函数
     * @param paperId 试卷ID
     * @param questionId 题目ID
     * @param score 分数
     */
    public PaperQuestion(Integer paperId, Long questionId, BigDecimal score) {
        this.paperId = paperId;
        this.questionId = questionId;
        this.score = score;
    }
} 