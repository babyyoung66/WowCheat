package com.cinle.wowcheat.WebSocket;

/**
 * @Author JunLe
 * @Time 2022/3/23 10:58
 * socket服务相关参数
 */
public class SocketConstants {


    /**
     * 连接地址
     */
    public static final String CONNECT_PATH= "/WowCheat";

    /**
     * topic订阅
     */
    public static final String TOPIC_SUBSCRIBE = "/topic";

    public static final String USER_SUBSCRIBE= "/user";

    /**
     * 前端个人订阅格式：前缀 + uuid + 后缀
     * 用户订阅前缀
     */
    public static final String USER_SUBSCRIBE_Prefix = "/user/";

    /**
     * 发送端对端消息时的 destination 参数
     * 用户订阅后缀
     */
    public static final String USER_SUBSCRIBE_Suffix = "/personal";


    /**
     * controller访问前缀
     * 即发送消息api前缀
     */
    public static final String ApplicationDestinationPrefixes = "/socket";


    /**
     * 限制消息长度
     */
    public static int CONTENT_LIMIT = 300;


    /**
     * 消息数记录间隔
     */
    public static long LIMIT_SECOND = 60 * 1000;

    /**
     * LIMIT_SECOND 时间内允许发送的消息数量
     */
    public static int LIMIT_TOTAL = 10;
}
