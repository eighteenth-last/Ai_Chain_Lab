package com.gpt.server.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Controller
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Test控制器
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello! AI-Chain-Lab 服务器运行正常！";
    }
    
    @GetMapping("/status")
    public String status() {
        return "服务器状态：正常运行中...";
    }
}