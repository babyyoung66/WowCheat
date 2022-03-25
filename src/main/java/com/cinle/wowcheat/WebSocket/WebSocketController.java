package com.cinle.wowcheat.WebSocket;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Model.CustomerMessage;
import com.cinle.wowcheat.Utils.EscapeHtmlUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * @Author JunLe
 * @Time 2022/3/23 15:05
 */
@Controller
public class WebSocketController {

    @Autowired
    SendSocketMessageServices socketMessageServices;
    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sendMessage")
    public void test(SocketUserPrincipal principal , CustomerMessage message){

        if ("personal".equals(message.getMsgType())){
            socketMessageServices.sendToUser(principal,message);
        }
        if ("group".equals(message.getMsgType())){
            socketMessageServices.sendToGroup(principal,message);
        }

    }
}
