package com.cinle.wowcheat.Constants;

/**
 * @Author JunLe
 * @Time 2022/2/24 14:02
 */
public class MyContans {
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


    /**
     * Token过期时间
     * 7天
     */
    public static final long TOKEN_Expiration = 7 * 24 * 60 * 60 * 1000;

    /**
     * Token 密钥
     */
    public static final String TOKEN_PRIVATE_KEY = "eyJzdWIiOiIxMjMiLCJpc3MiOiJXT1dfQ0hFQVQiLCJleHAiOjE2NDU4MTcxNTAsImlhdCI6MTY0NTc3Mzk1MH0";


    public static final String TOKEN_ISSUER = "WOW_CHEAT";

    public static final String FILE_BASE_URL = "https://www.cinle.icu:9999";


}
