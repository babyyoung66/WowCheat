package com.cinle.wowcheat.Security;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Vo.AjaxResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author JunLe
 * @Time 2022/2/20 23:20
 * 自定义验证成功处理类
 */
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    //spring自带的json生成
    private static ObjectMapper objectMapper = new ObjectMapper();
    private final Logger log = LoggerFactory.getLogger(LoginSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        AjaxResponse ajaxResponse = new AjaxResponse();
        //说明是以json的形式传递参数
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
            response.setContentType("application/json;charset=UTF-8");
            //从认证体获取用户信息
            CustomerUserDetails userDetail = (CustomerUserDetails) authentication.getPrincipal();
            Object user = JSON.toJSON(userDetail);
            /*后期使用jwt*/
            response.getWriter().write(objectMapper.writeValueAsString(ajaxResponse.success().setMessage("登录成功!").setData(user)));
            response.getWriter().flush();
            response.getWriter().close();
            log.info("用户uuid:{} 已从主机 {}:{} 登录服务......",userDetail.getUuid(),request.getRemoteHost(),request.getRemotePort());
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }

    }
}
