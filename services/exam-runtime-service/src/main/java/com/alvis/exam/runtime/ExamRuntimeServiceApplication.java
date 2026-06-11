package com.alvis.exam.runtime;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.alvis.exam")
@MapperScan("com.alvis.exam.runtime.repository")
public class ExamRuntimeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExamRuntimeServiceApplication.class, args);
    }
}
