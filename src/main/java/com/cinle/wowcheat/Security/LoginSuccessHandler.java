package com.cinle.wowcheat.Security;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Service.JwtTokenService;
import com.cinle.wowcheat.Vo.AjaxResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @Author JunLe
 * @Time 2022/2/20 23:20
 * 自定义登录成功处理类
 */
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final Logger log = LoggerFactory.getLogger(LoginSuccessHandler.class);
    @Autowired
    private JwtTokenService jwtTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        AjaxResponse ajaxResponse = new AjaxResponse();
        //说明是以json的形式传递参数
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
            response.setContentType("application/json;charset=UTF-8");
            //从认证体获取用户信息
            CustomerUserDetails userDetail = (CustomerUserDetails) authentication.getPrincipal();
            /*使用uuid生成token*/
            List<String> roles = new ArrayList<>();
            Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) userDetail.authorities;
            Iterator it = authorities.iterator();
            if (authorities.iterator().hasNext()) {
                roles.add(it.next().toString());
            }

            String token = jwtTokenService.createToken(userDetail.getUuid(),roles);
            Map map = new HashMap();
            map.put("token",token);
            map.put("user",userDetail);
            response.getWriter().write(JSON.toJSONString(ajaxResponse.success().setMessage("登录成功!").setData(map)));
            response.getWriter().flush();
            response.getWriter().close();
            log.info("用户uuid: {} 已从主机 {}:{} 登录服务......",userDetail.getUuid(),request.getRemoteHost(),request.getRemotePort());
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }

    }
}
