package com.cinle.wowcheat.WebSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/3/22 15:33
 * 方法说明参考：https://www.jianshu.com/p/9103c9c7e128
 */

//使用stomp
@EnableWebSocketMessageBroker
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Autowired
    InboundInterceptor inboundInterceptor;

    /**
     * 自定义线程池
     */
    @Autowired
    WebSocketTaskExecutor socketExecutor;



    /**
     * 前端连接地址,可设置多个
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(SocketConstants.CONNECT_PATH).setAllowedOriginPatterns("*").withSockJS();

    }


    /**
     * 配置消息代理，哪种路径的消息会进行代理处理
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 自定义调度器，用于控制心跳线程
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        // 线程池线程数，心跳连接开线程
        taskScheduler.setPoolSize(1);
        // 线程名前缀
        taskScheduler.setThreadNamePrefix("websocket-heartbeat-thread-");
        // 初始化
        taskScheduler.initialize();

        //服务端发送消息给客户端的域,多个用逗号隔开
        registry.enableSimpleBroker(SocketConstants.TOPIC_SUBSCRIBE,SocketConstants.USER_SUBSCRIBE)
                .setHeartbeatValue(new long[]{30000,30000})
                .setTaskScheduler(taskScheduler);
        //定义一对一推送的时候前缀，默认为/user/
        registry.setUserDestinationPrefix(SocketConstants.USER_SUBSCRIBE_Prefix);
        //controller请求前缀
        registry.setApplicationDestinationPrefixes(SocketConstants.ApplicationDestinationPrefixes);

    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(inboundInterceptor);
        registration.taskExecutor(socketExecutor.getExecutor());
    }


    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        //消息限制
        registry.setMessageSizeLimit(2*1024 * 1024);
        //缓存大小
        registry.setSendBufferSizeLimit(5*1024 * 1024);
        //发送超时
        registry.setSendTimeLimit(5000);

    }


    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor(socketExecutor.getExecutor());
    }


}
