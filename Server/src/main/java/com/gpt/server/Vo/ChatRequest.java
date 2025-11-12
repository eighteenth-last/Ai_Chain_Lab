package com.gpt.server.Vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Vo
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: ChatRequest数据传输对象
 * @Version: 1.0
 */
@Data
@Builder
public class ChatRequest implements Serializable {
    /**
     * 模型名称
     */
    private String model; // 模型名称

    /**
     * 消息列表
     */
    private List<ChatMessage> messages; // 消息列表

    /**
     * 是否使用流式传输
     */
    private boolean stream = false; // 是否使用流式传输

    /**
     * 生成文本的随机性，介于0和1之间
     */
    private double temperature = 0.3; // 温度参数

    /**
     * 最大生成token数
     */
    private Integer maxTokens; // 最大生成token数

    private static final long serialVersionUID = 1L; // 序列化版本UID
} 