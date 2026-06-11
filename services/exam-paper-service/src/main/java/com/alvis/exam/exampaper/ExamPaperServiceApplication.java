package com.alvis.exam.exampaper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.alvis.exam")
@MapperScan("com.alvis.exam.exampaper.repository")
public class ExamPaperServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExamPaperServiceApplication.class, args);
    }
}
