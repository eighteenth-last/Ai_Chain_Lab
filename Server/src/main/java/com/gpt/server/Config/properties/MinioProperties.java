package com.gpt.server.Config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Config.properties
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Minio配置属性类
 * @Version: 1.0
 */

@Data
@Component
@ConfigurationProperties(prefix = "minio") // 批量读取
public class MinioProperties {
    private String accessKey;

    private String secretKey;

    private String bucketName;

    private String endPoint;

    private String region;

}
