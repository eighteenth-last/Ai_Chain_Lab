package com.gpt.server.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-10-26
 * @BelongsProject: Server
 * @Description: 在 Spring Boot 启动时自动输出 API 文档地址
 * @Version: 1.0
 */
@Component
public class ApiDocumentationRunner implements ApplicationRunner {

    @Autowired
    private Environment env;

    @Override
    public void run(ApplicationArguments args) {
        String port = env.getProperty("server.port", "8080");
        System.out.println("\n=== 📚 API 文档地址 ===");
        System.out.println("🌐 Knife4j UI: http://localhost:" + port + "/doc.html");
        System.out.println("📄 Swagger UI: http://localhost:" + port + "/swagger-ui.html");
        System.out.println("========================\n");
    }
}

