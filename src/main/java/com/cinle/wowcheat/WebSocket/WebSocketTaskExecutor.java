package com.cinle.wowcheat.WebSocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author JunLe
 * @Time 2022/3/23 1:43
 */
@EnableAsync
@Configuration
public class WebSocketTaskExecutor {

    @Bean("socketExecutor")
    public ThreadPoolTaskExecutor getExecutor(){
        ThreadPoolTaskExecutor  executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(6);
        executor.setMaxPoolSize(16);
        executor.setKeepAliveSeconds(60);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setQueueCapacity(300);
        executor.setThreadNamePrefix("WebSocketExecutor");
        executor.initialize();
        return executor;
    }
}
