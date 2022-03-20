package com.cinle.wowcheat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync//启用异步处理
@SpringBootApplication
public class WowCheatApplication {

    public static void main(String[] args) {
        SpringApplication.run(WowCheatApplication.class, args);
    }

}
