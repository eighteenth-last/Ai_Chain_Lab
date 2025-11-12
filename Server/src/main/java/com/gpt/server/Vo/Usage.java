package com.gpt.server.Vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Vo
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Usage数据传输对象
 * @Version: 1.0
 */
@Data
public class Usage implements Serializable {
    /**
     * 提示词Token数
     */
    @JsonProperty("prompt_tokens")
    private int promptTokens; // 提示词Token数

    /**
     * 完成Token数
     */
    @JsonProperty("completion_tokens")
    private int completionTokens; // 完成Token数

    /**
     * 总Token数
     */
    @JsonProperty("total_tokens")
    private int totalTokens; // 总Token数

    private static final long serialVersionUID = 1L; // 序列化版本UID
} 