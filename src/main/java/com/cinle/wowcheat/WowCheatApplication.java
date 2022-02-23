package com.cinle.wowcheat;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.CrossOrigin;

@EnableAsync//启用异步处理
@SpringBootApplication
@Mapper
public class WowCheatApplication {

    public static void main(String[] args) {
        SpringApplication.run(WowCheatApplication.class, args);
    }

}
