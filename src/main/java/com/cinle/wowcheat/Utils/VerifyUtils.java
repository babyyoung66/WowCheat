package com.cinle.wowcheat.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;


/**
 * @Author JunLe
 * @Time 2022/2/21 16:31
 */
@Component
public class VerifyUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final String KEY_HEAD = "VerifyCode:";


    private final Logger log = LoggerFactory.getLogger(VerifyUtils.class);

    public boolean setEmailCode(String email, String code) {
        String key = KEY_HEAD + email;
        /*存入redis，过期时间300s*/
        redisTemplate.opsForValue().set(key, code, 300, TimeUnit.SECONDS);
        log.info("向Redis存入邮箱验证码key:{} -- value:{}", key , code);
        return true;

    }

    /**
     * @param email 注册邮箱
     * @param code  验证码
     * @return
     */
    public boolean checkEmailCode(String email, String code) {
        String key = KEY_HEAD + email;
        String co = (String) redisTemplate.opsForValue().get(key);
        if (co != null && co.equals(code)) {
            /*返回true 并将redis状态改为true，保存5分钟*/
            redisTemplate.opsForValue().set(key, "true", 300, TimeUnit.SECONDS);
            log.info("验证成功，将Redis中邮箱验证码key:{}的状态设置为true", key );
            return true;
        }
        return false;
    }

    /**
     * 校验用户是否通过前端验证了邮箱
     * @param email
     * @return
     */
    public boolean isEmailCheckSuccess(String email) {
        String key = KEY_HEAD + email;
        String status = (String) redisTemplate.opsForValue().get(key);
        if (status != null && status.equals("true")) {
            return true;
        }
        return false;
    }

    /**
     * 获取验证码剩余时间
     * 从redis中获取key对应的过期时间;
     * 如果该值有过期时间，就返回相应的过期时间;
     * 如果该值没有设置过期时间，就返回-1;
     * 如果没有该值，就返回-2;
     */
    public Long getEmailCodeTTL(String email) {
        String key = KEY_HEAD + email;
        return redisTemplate.opsForValue().getOperations().getExpire(key, TimeUnit.SECONDS);
    }
}
