package com.cinle.wowcheat.WebSocket;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Enum.RoleEnum;
import com.cinle.wowcheat.Model.CustomerMessage;
import com.cinle.wowcheat.Model.Friends;
import com.cinle.wowcheat.Service.FriendsServices;
import com.cinle.wowcheat.Service.MessageServices;
import com.cinle.wowcheat.Utils.EscapeHtmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/3/23 17:51
 */
@Service
public class SendSocketMessageServicesImpl implements SendSocketMessageServices {
    @Autowired
    SimpMessagingTemplate messagingTemplate;
    @Autowired
    FriendsServices friendsServices;
    @Autowired
    MessageServices messageServices;

    @Override
    public void sendToUser(SocketUserPrincipal principal, CustomerMessage customerMessage) {
        SocketMessage message = new SocketMessage();
        String shelfUuid = principal.getName();
        if (customerMessage.getContent().trim().length() > SocketConstants.CONTENT_LIMIT){
            message.error().setErrorMessage("文字超出限制长度！");
            messagingTemplate.convertAndSendToUser(shelfUuid, SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(message, true));
            return;
        }
        //xss清洗
        String mess = EscapeHtmlUtils.escapeHtml(JSON.toJSONString(customerMessage));
        CustomerMessage newMess =  JSON.parseObject(mess,CustomerMessage.class);
        customerMessage = newMess;
        customerMessage.setFrom(shelfUuid);
        customerMessage.setTime(new Date());
        //给自己发消息
        if(shelfUuid.equals(customerMessage.getTo())){
            message.success().setMessage(customerMessage);
            messagingTemplate.convertAndSendToUser(shelfUuid, SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(message, true));
            //入库
            messageServices.saveMessage(customerMessage,"personal");
            return;
        }
        Friends shelfInfo = friendsServices.findFriend(shelfUuid, customerMessage.getTo());
        if (shelfInfo == null) {
            message.error().setCode(404).setErrorMessage("对方不是您的好友！");
            messagingTemplate.convertAndSendToUser(shelfUuid, SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(message, true));
            return;
        }
        Friends friendsInfo = friendsServices.findFriend(customerMessage.getTo(), shelfUuid);
        switch (shelfInfo.getStatus()) {
            //好友状态（1正常，2屏蔽，3拉黑，4被对方删除）默认1
            case 1:
                switch (friendsInfo.getStatus()) {
                    //双方都为1时则发送
                    case 1:
                        message.success().setMessage(customerMessage);
                        messagingTemplate.convertAndSendToUser(customerMessage.getTo(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(message, true));
                        //入库
                        messageServices.saveMessage(customerMessage,"personal");
                        break;
                    case 2:
                        message.success().setMessage(customerMessage);
                        break;
                    case 3:
                        message.error().setErrorMessage("您已被对方拉黑！");
                        break;
                    default:
                        message.error().setErrorMessage("无法满足您的请求！");
                        break;
                }
                break;
            case 2:
                message.error().setErrorMessage("您已将对方屏蔽！");
                break;
            case 3:
                message.error().setErrorMessage("对方已被您拉黑！");
                break;
            case 4:
                message.error().setErrorMessage("您不是对方的好友！");
                break;
            default:
                message.error().setErrorMessage("无法满足您的请求！");
                break;
        }
        //destination 参数必须设置，前端订阅格式为 /user/ + uuid + /personal
        messagingTemplate.convertAndSendToUser(shelfUuid, SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(message, true));

    }

    @Override
    public void sendToGroup(SocketUserPrincipal principal, CustomerMessage customerMessage) {

    }

    @Override
    public void sendTopic(SocketUserPrincipal principal, CustomerMessage customerMessage) {
        SocketMessage message = new SocketMessage();
        List<String> roles = principal.getRoles();
        //非管理员不允许发送topic消息
        if (!roles.contains(RoleEnum.ADMIN.getName())) {
             message.error().setErrorMessage("您无权限操作！");
             messagingTemplate.convertAndSendToUser(principal.getName(),SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(message, true));
             return;
        }
        customerMessage.setTime(new Date());
        messagingTemplate.convertAndSend(SocketConstants.TOPIC_SUBSCRIBE,JSON.toJSONString(message, true));
    }
}
