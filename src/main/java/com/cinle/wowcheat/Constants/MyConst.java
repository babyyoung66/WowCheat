package com.cinle.wowcheat.Constants;

/**
 * @Author JunLe
 * @Time 2022/2/24 14:02
 */
public class MyConst{
    /**
     * 邮箱验证重发等待时间
     * 单位秒
     */
    public static final long EMAIL_CODE_WAIT_TIME = 120;

    /**
     * 验证码存活时间
     */
    public static final long CODE_KEEPALIVE_TIME = 300;


    /**
     * 验证码长度
     */
    public static final int EMAIL_CODE_LENGTH = 6;


    /** 秒
     * Token过期时间
     * 7 * 24 * 60 * 60 秒
     */
    public static final long TOKEN_Expiration = 7 * 24 * 60 * 60;

    /**
     * Token 密钥
     */
    public static final String TOKEN_PRIVATE_KEY = "eyJzdWIiOiIxMjMiLCJpc3MiOiJXT1dfQ0hFQVQiLCJleHAiOjE2NDU4MTcxNTAsImlhdCI6MTY0NTc3Mzk1MH0";


    /**
     * token签名
     */
    public static final String TOKEN_ISSUER = "WOW_CHEAT";


    /**
     * 默认群聊id
     * 用户注册后默认添加
     */
    public static final String DEFAULT_GROUP_ID = "sysTestGroup001";

    /**
     * 默认管理员id
     * 用户注册后默认添加
     */
    public static final String DEFAULT_ADMIN_ID = "62124fee77b646417ced30b9";

}
