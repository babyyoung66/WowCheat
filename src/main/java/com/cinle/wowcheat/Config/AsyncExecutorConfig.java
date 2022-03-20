package com.cinle.wowcheat.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author JunLe
 * @Time 2022/3/16 0:28
 */

@EnableAsync
@Configuration
public class AsyncExecutorConfig {
    @Bean("AsyncExecutor")
    public Executor AsyncExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(8);
        taskExecutor.setMaxPoolSize(16);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix("AsyncExecutor--");
        taskExecutor.setQueueCapacity(300);
        //拒绝策略
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }
}
