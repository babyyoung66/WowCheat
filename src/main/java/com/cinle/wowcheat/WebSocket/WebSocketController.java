package com.cinle.wowcheat.WebSocket;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Model.CustomerMessage;
import com.cinle.wowcheat.Vo.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Date;

/**
 * @Author JunLe
 * @Time 2022/3/23 15:05
 */
@Controller
public class WebSocketController {

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("test")
    public void test(SocketUserPrincipal principal , CustomerMessage message){
        System.out.println("message = " + message);
        AjaxResponse response = new AjaxResponse();
        if (!principal.getRoles().contains("admin")){
           response.error().setMessage("无权限！");
        }else {
            message.setFrom(principal.getName());
            message.setTime(new Date());
            response.success().setData(message);
        }
        //发送给 62124fee77b646417ced30b9 ，会拼接成/user/62124fee77b646417ced30b9/personal，这个是前端真实订阅地址
        //destination 参数必须设置，前端订阅格式为 /user/ + uuid + /personal
        messagingTemplate.convertAndSendToUser(message.getTo(),SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(response,true));
        //广播
       // messagingTemplate.convertAndSend("/topic","广播内容");
    }
}
