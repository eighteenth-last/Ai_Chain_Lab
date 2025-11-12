package com.gpt.server.Vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Vo
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: ResponseMessage数据传输对象
 * @Version: 1.0
 */
@Data
public class ResponseMessage implements Serializable {
    /**
     * 角色
     */
    private String role; // 角色

    /**
     * 内容
     */
    private String content; // 内容

    private static final long serialVersionUID = 1L; // 序列化版本UID
} 