package com.gpt.server.Vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Vo
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: ChatChoice数据传输对象
 * @Version: 1.0
 */
@Data
public class ChatChoice implements Serializable {
    /**
     * 索引
     */
    private int index; // 索引

    /**
     * 消息
     */
    private ResponseMessage message; // 消息

    /**
     * 结束原因
     */
    @JsonProperty("finish_reason")
    private String finishReason; // 结束原因

    private static final long serialVersionUID = 1L; // 序列化版本UID
} 