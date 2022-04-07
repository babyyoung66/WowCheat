package com.cinle.wowcheat.WebSocket;

/**
 * @Author JunLe
 * @Time 2022/4/7 9:50
 * Socket消息类型，前端使用适配器判断类型
 * 然后更新到对应本地数据
 */
public enum SocketMessageType {
    /**
     * 普通消息
     * */
    cheat,

    /**
     * 公告消息
     * */
    notice,

    /**
     * 更新好友
     * */
    updateFriend,

    /**
     * 发送好友请求
     * */
    friendRequest,


    updateGroup;


}
