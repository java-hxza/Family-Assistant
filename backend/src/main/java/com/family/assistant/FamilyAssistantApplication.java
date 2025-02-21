package com.family.assistant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.family.assistant.mapper")
public class FamilyAssistantApplication {
    public static void main(String[] args) {
        SpringApplication.run(FamilyAssistantApplication.class, args);
    }
} 