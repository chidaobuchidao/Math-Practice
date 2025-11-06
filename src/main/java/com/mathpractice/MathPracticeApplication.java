package com.mathpractice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mathpractice.mapper")
public class MathPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MathPracticeApplication.class, args);
    }

}
