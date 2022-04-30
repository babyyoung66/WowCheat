package com.cinle.wowcheat.WebSocket;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Model.CheatMessage;
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
    public void sendMessage(SocketUserPrincipal principal, CheatMessage message) {
        //System.out.println("message = " + message);
        if (principal.getLimitTotal() > SocketConstants.LIMIT_TOTAL) {
            SocketMessage socketMessage = new SocketMessage();
            String res = String.format("操作过于频繁，请%d秒后再尝试！", SocketConstants.LIMIT_SECOND % 1000);
            socketMessage.error().setErrorMessage(res);
            messagingTemplate.convertAndSendToUser(principal.getName(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(socketMessage, true));
            return;
        }
        message.setContentType("text");
        socketMessageServices.sendText(principal,message);

    }
}
