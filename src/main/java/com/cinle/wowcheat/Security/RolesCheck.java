package com.cinle.wowcheat.Security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author JunLe
 * @Time 2022/2/21 10:03
 * 权限过滤
 * 需配置权限对应的url使用
 */
@Component("checkRoles")
public class RolesCheck {

    public boolean hasPermission(HttpServletRequest request, Authentication authentication){

        //Authentication authentication为当前用户的认证体信息
        Object Principal=authentication.getPrincipal();//当前认证的主体信息(用户基本信息)
        if (Principal instanceof CustomerUserDetails){
            CustomerUserDetails userinfo= ((CustomerUserDetails) Principal);//将请求体转换为用户实例

            SimpleGrantedAuthority simpleGrantedAuthority=new SimpleGrantedAuthority(request.getRequestURI());//当前访问的资源
            return  userinfo.getAuthorities().contains(simpleGrantedAuthority);//使用contains判断当前访问的资源是否存在权限中
        }
        return false;
    }
}
