package com.gpt.server.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器 - 用于验证基本功能
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