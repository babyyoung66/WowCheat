package com.cinle.wowcheat.Tools;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;

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

    public static Collection<? extends GrantedAuthority> getCurrentUserAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }
}
