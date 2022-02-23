package com.cinle.wowcheat.Security;

import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author JunLe
 * @Time 2022/2/23 9:59
 * 从JSON中获取rememberme
 */
public class CustomerRememberMeServices extends PersistentTokenBasedRememberMeServices {


    public CustomerRememberMeServices(String key, UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
        super(key, userDetailsService, tokenRepository);
    }

    @Override
    protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {

        //判断请求是否为JSON
        if(request != null && request.getMethod().equalsIgnoreCase("POST") && request.getContentType() != null &&
                (request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON_UTF8_VALUE) || request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE))) {
            // 此时我们之前在账号密码拦截器中向Attribute中放的数据可以再次取出来使用了
            // 如果使用request.getInputStream()获取流会发现流已经关闭会报错
            Boolean rememberMe =(Boolean) request.getAttribute("rememberMe");
            if(rememberMe){
                return true;
            }
        }
        //否则调用原本的自我记住功能
        return super.rememberMeRequested(request, parameter);
    }



}
