package com.gpt.server.Vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Vo
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: LoginRequest数据传输对象
 * @Version: 1.0
 */
@Data
@Schema(description = "用户登录请求参数")
public class LoginRequestVo {
    
    @Schema(description = "用户名", 
            example = "admin", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    private String username; // 用户名
    
    @Schema(description = "登录密码", 
            example = "123456", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password; // 密码
} 