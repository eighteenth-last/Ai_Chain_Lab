package com.gpt.server.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Config
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: ApiDocumentationRunner配置类
 * @Version: 1.0
 */
@Component
public class ApiDocumentationRunner implements ApplicationRunner {

    @Autowired
    private Environment env;

    @Override
    public void run(ApplicationArguments args) {
        String port = env.getProperty("server.port", "8080");
        System.out.println("\n=== 📚 API 文档地址 ===========================================");
        System.out.println("🌐 Knife4j UI: http://localhost:" + port + "/doc.html");
        System.out.println("📄 Swagger UI: http://localhost:" + port + "/swagger-ui.html");
        System.out.println("==============================================================\n");
    }
}

