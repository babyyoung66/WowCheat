package com.cinle.wowcheat.Tools;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @Author JunLe
 * @Time 2022/3/5 19:30
 */
public class SecurityContextUtils {


    /**
     * 获取security上下文认证主体
     * 本项目的认证主体为用户UUID
     * @return
     */
    public static String getCurrentUserUUID(){
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
