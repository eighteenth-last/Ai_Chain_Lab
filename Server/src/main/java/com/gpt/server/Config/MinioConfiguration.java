package com.gpt.server.Config;

import com.gpt.server.Config.properties.MinioProperties;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Config
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Miniouration配置类
 * @Version: 1.0
 */
@Slf4j
@Configuration
public class MinioConfiguration {
    @Autowired
    private MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(minioProperties.getEndPoint())
                        .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                        .region(minioProperties.getRegion())
                        .build();
        log.info("minioClient连接成功，连接信息为: {}", minioClient);
        return minioClient;
    }
}
