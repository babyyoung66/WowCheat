package com.cinle.wowcheat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@MapperScan("com.*.*.Dao")
@EnableAsync//启用异步处理
@SpringBootApplication
public class WowCheatApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(WowCheatApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(WowCheatApplication.class);
    }
}

