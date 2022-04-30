package com.cinle.wowcheat.WebSocket;

import com.cinle.wowcheat.Model.CheatMessage;
import com.cinle.wowcheat.Model.MyUserDetail;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author JunLe
 * @Time 2022/3/23 16:25
 *
 * 发送消息接口
 */
public interface SendSocketMessageServices {

    void sendText(SocketUserPrincipal principal, CheatMessage cheatMessage);

    //void sendToUser(SocketUserPrincipal principal, CheatMessage cheatMessage);

    //void sendToGroup(SocketUserPrincipal principal, CheatMessage cheatMessage);


    void sendTopic(SocketUserPrincipal principal, CheatMessage cheatMessage);

    void sendFile(SocketUserPrincipal principal, CheatMessage cheatMessage, MultipartFile file);

    void sendWelcomeMessage(MyUserDetail user);
}
