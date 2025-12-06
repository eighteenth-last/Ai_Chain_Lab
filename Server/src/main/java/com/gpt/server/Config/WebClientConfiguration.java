package com.gpt.server.Config;

import com.gpt.server.Config.properties.DeepSeekProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Vo
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: WebClient
 * @Version: 1.0
 */

@Configuration
@EnableConfigurationProperties(DeepSeekProperties.class)
public class WebClientConfiguration {

    @Autowired
    private DeepSeekProperties deepSeekProperties;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().
                baseUrl(deepSeekProperties.getUri())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", "Bearer " + deepSeekProperties.getApiKey())
                .build();
    }
}
