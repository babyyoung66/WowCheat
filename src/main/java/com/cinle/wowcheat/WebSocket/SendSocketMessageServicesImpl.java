package com.cinle.wowcheat.WebSocket;

import com.cinle.wowcheat.Model.CustomerMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * @Author JunLe
 * @Time 2022/3/23 17:51
 */
@Service
public class SendSocketMessageServicesImpl implements SendSocketMessageServices{
    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendToUser(SocketUserPrincipal principal, CustomerMessage customerMessage) {

    }

    @Override
    public void sendToGroup(SocketUserPrincipal principal, CustomerMessage customerMessage) {

    }

    @Override
    public void sendTopic(SocketUserPrincipal principal, CustomerMessage customerMessage) {

    }
}
