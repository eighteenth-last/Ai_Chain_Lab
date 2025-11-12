package com.gpt.server.Config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Config
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Mybatis配置类
 * @Version: 1.0
 */
@MapperScan("com.gpt.server.Mapper")
@Configuration
public class MybatisConfig {
}
