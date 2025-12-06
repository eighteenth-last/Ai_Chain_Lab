package com.gpt.server.Config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Vo
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: 模型交互的常用参数
 * @Version: 1.0
 */

@Component
@ConfigurationProperties(prefix = "deepseek.api")
@Data
public class DeepSeekProperties {

    private String model;
    private String uri;
    private String apiKey;
    private Integer maxTokens;
    private Double temperature;

}
