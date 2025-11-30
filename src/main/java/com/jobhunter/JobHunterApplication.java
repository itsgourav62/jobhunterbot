package com.jobhunter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties
public class JobHunterApplication {
    
    public static void main(String[] args) {
        System.out.println("🚀 JobHunter Bot - Full Backend System Starting...");
        SpringApplication.run(JobHunterApplication.class, args);
        System.out.println("✅ JobHunter Backend ready at http://localhost:8080");
    }
}