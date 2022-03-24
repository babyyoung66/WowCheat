package com.cinle.wowcheat.WebSocket;

import com.cinle.wowcheat.Model.CustomerMessage;

import java.security.Principal;

/**
 * @Author JunLe
 * @Time 2022/3/23 16:25
 *
 * 发送消息接口
 */
public interface SendSocketMessageServices {

    void sendToUser(SocketUserPrincipal principal,CustomerMessage customerMessage);

    void sendToGroup(SocketUserPrincipal principal,CustomerMessage customerMessage);

    void sendTopic(SocketUserPrincipal principal,CustomerMessage customerMessage);

}
