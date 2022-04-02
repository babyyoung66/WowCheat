package com.cinle.wowcheat.WebSocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

/**
 * @Author JunLe
 * @Time 2022/3/30 15:51
 * 消息输出时拦截器
 */
@Component
public class InterceptorOutbound implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        return message;
    }
}
