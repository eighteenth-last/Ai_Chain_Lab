package com.gpt.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: ServerApplication类
 * @Version: 1.0
 */
@Slf4j
@SpringBootApplication
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
        System.out.println(" Ai 链习室启动成功 ᕙ( ＾‿ゝ＾ c)       \n"+
                "     _______      __   __                      \n"+
                "   / ____(_)___ _/ /_  / /____  ___  ____      \n"+
                "  / __/ / / __ `/ __ \\/ __/ _ \\/ _ \\/ __ \\ \n"+
                " / /___/ / /_/ / / / / /_/  __/  __/ / / /     \n"+
                "/_____/_/\\__, /_/ /_/\\__/\\___/\\___/_/ /_/  \n"+
                "        /____/                                  ");
    }
}
