package com.gpt.server.Entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.math.BigDecimal;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Entity
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Question实体类
 * @Version: 1.0
 */
@Data  // Lombok注解：自动生成所有getter、setter方法
@TableName("questions")  // 对应数据库表：questions
@Schema(description = "题目信息")
public class Question extends BaseEntity {
    
    @Schema(description = "题目标题内容", 
            example = "以下关于Java面向对象编程的说法正确的是？")
    private String title;
    
    @Schema(description = "题目类型", 
            example = "CHOICE", 
            allowableValues = {"CHOICE", "JUDGE", "TEXT"})
    private String type;
    
    @Schema(description = "是否为多选题，仅选择题有效", 
            example = "false")
    private Boolean multi;
    
    @Schema(description = "题目分类ID", 
            example = "1")
    private Long categoryId;
    
    @Schema(description = "题目难度等级", 
            example = "MEDIUM", 
            allowableValues = {"EASY", "MEDIUM", "HARD"})
    private String difficulty;
    
    @Schema(description = "题目默认分值", 
            example = "5")
    private Integer score;
    
    @Schema(description = "在特定试卷中的分值", 
            example = "10.0")
    @TableField(exist = false)  // 标记为非数据库字段
    private BigDecimal paperScore;
    
    @Schema(description = "题目解析，详细的答案说明", 
            example = "Java是面向对象编程语言，支持封装、继承、多态三大特性...")
    private String analysis;

    //多表
    @Schema(description = "选择题选项列表，包含A、B、C、D等选项")
    @TableField(exist = false)
    private List<QuestionChoice> choices;

    //多表
    @Schema(description = "题目答案信息，包含正确答案和评分标准")
    @TableField(exist = false)
    private QuestionAnswer answer;

    //多表
    @Schema(description = "题目所属分类信息")
    @TableField(exist = false)
    private Category category;
} 