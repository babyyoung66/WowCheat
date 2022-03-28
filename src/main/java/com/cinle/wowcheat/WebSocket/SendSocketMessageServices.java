package com.cinle.wowcheat.WebSocket;

import com.cinle.wowcheat.Model.CustomerMessage;
import org.springframework.web.multipart.MultipartFile;

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

    void sendFile(SocketUserPrincipal principal, CustomerMessage customerMessage, MultipartFile file);

}
