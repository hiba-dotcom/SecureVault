package com.securevault.passwordservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${service.auth.url}")
    private String authServiceUrl;

    @Value("${service.security.url}")
    private String securityServiceUrl;

    @Bean
    public WebClient authWebClient() {
        return WebClient.builder()
                .baseUrl(authServiceUrl)
                .build();
    }

    @Bean
    public WebClient securityWebClient() {
        return WebClient.builder()
                .baseUrl(securityServiceUrl)
                .build();
    }
}