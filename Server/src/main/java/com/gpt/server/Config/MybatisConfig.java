package com.gpt.server.Config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-10-26  18:23
 * @BelongsProject: Server
 * @Description: TODO
 * @Version: 1.0
 */
@MapperScan("com.gpt.server.Mapper")
@Configuration
public class MybatisConfig {
}
