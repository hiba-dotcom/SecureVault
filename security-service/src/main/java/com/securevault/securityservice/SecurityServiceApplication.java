package com.securevault.securityservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SecurityServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecurityServiceApplication.class, args);
    }
}