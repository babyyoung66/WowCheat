package com.cinle.wowcheat.Event;

import com.cinle.wowcheat.WebSocket.SocketMessage;
import org.springframework.context.ApplicationEvent;

/**
 * @Author JunLe
 * @Time 2022/4/7 9:43
 */
public class SendSocketMessageEvent extends ApplicationEvent {
    public SendSocketMessageEvent(SocketMessage source) {
        super(source);
    }
}
