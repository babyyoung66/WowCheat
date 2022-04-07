package com.cinle.wowcheat.Event;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Model.FriendsRequest;
import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Service.FriendRequestServices;
import com.cinle.wowcheat.Service.UserServices;
import com.cinle.wowcheat.Utils.SecurityContextUtils;
import com.cinle.wowcheat.WebSocket.SocketConstants;
import com.cinle.wowcheat.WebSocket.SocketMessage;
import com.cinle.wowcheat.WebSocket.SocketMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Author JunLe
 * @Time 2022/4/7 9:45
 * <p>
 * 发布系统消息，实时更新用户数据等
 * @see SocketMessageType
 * 根据对应类型执行
 */
@Component
public class SendSocketMessageEventListener {
    @Autowired
    UserServices userServices;
    @Autowired
    SimpMessagingTemplate messagingTemplate;
    @Autowired
    FriendRequestServices friendRequestServices;


    @Async("AsyncExecutor")
    @EventListener(SendSocketMessageEvent.class)
    public void SendSocketMessage(SendSocketMessageEvent event) {
        SocketMessage socketMessage = (SocketMessage) event.getSource();
        switch (socketMessage.getType()) {
            case cheat:
                break;
            case notice:
                sendTopic(socketMessage);
                break;
            case updateFriend:
                sendUpdateFriend(socketMessage);
                break;
            case friendRequest:
                sendFriendRequest(socketMessage);
                break;
            case updateGroup:
                sendUpdateGroup(socketMessage);
                break;
        }

    }

    /**
     * 同意好友请求时
     * 更新好友列表
     * 双向
     */
    private void sendUpdateFriend(SocketMessage socketMessage) {
        FriendsRequest request = (FriendsRequest) socketMessage.getMessage();
        //给自己，对方的信息
        MyUserDetail toShelf = userServices.selectByFriendUuid(request.getReceiverUuid(), request.getRequestUuid());
        socketMessage.setMessage(toShelf);
        messagingTemplate.convertAndSendToUser(request.getReceiverUuid(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSON(socketMessage));
        //给对方，自己的信息
        MyUserDetail toTarget = userServices.selectByFriendUuid(request.getRequestUuid(), request.getReceiverUuid());
        socketMessage.setMessage(toTarget);
        messagingTemplate.convertAndSendToUser(request.getRequestUuid(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSON(socketMessage));
    }

    /**
     * 更新好友请求列表
     * 双向
     */
    private void sendFriendRequest(SocketMessage socketMessage) {
        FriendsRequest request = (FriendsRequest) socketMessage.getMessage();
        //
        if (request.getRequestStatus() == 1) {
            return;
        }
        FriendsRequest toShelf = friendRequestServices.selectWithUserInfoByEachUuid(request.getRequestUuid(), request.getReceiverUuid(), request.getReceiverUuid());
        socketMessage.setMessage(toShelf);
        messagingTemplate.convertAndSendToUser(request.getRequestUuid(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSON(socketMessage));

        FriendsRequest toTarget = friendRequestServices.selectWithUserInfoByEachUuid(request.getRequestUuid(), request.getReceiverUuid(), request.getRequestUuid());
        socketMessage.setMessage(toTarget);
        messagingTemplate.convertAndSendToUser(request.getReceiverUuid(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSON(socketMessage));


    }

    /**
     * 给新入群者发送更新群聊消息
     */
    private void sendUpdateGroup(SocketMessage socketMessage) {

    }


    private void sendTopic(SocketMessage socketMessage) {

    }
}
