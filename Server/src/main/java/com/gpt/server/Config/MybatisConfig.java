package com.gpt.server.Config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
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
    @Bean
    public MybatisPlusInterceptor plusInterceptor() {
        MybatisPlusInterceptor plusInterceptor = new MybatisPlusInterceptor();

        // 添加分页插件
        plusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return plusInterceptor;
    }
}
