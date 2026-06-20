package com.example.ailearning;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.ailearning.module")
public class AiLearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiLearningApplication.class, args);
    }
}
