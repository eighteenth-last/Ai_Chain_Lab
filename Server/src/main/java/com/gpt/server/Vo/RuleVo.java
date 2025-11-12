package com.gpt.server.Vo;

import com.gpt.server.Entity.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Vo
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Rule数据传输对象
 * @Version: 1.0
 */
@Data
@Schema(description = "AI组卷规则配置")
public class RuleVo {

    @Schema(description = "题目类型", 
            example = "CHOICE", 
            allowableValues = {"CHOICE", "JUDGE", "TEXT"})
    private QuestionType type;

    @Schema(description = "指定的题目分类ID列表，为空则不限制分类", 
            example = "[1, 2, 3]")
    private List<Integer> categoryIds;

    @Schema(description = "需要抽取的题目数量", 
            example = "10", 
            minimum = "1")
    private Integer count;

    @Schema(description = "每道题目的分数", 
            example = "5", 
            minimum = "1")
    private Integer score;
} 