package com.cinle.wowcheat.Security;

import com.cinle.wowcheat.Vo.AjaxResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author JunLe
 * @Time 2022/2/20 23:04
 * 自定义验证失败处理类
 * */
@Component
public class LoginFailHandler extends SimpleUrlAuthenticationFailureHandler {

    private static ObjectMapper objectMapper=new ObjectMapper();


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //说明是以json的形式传递参数
        AjaxResponse ajaxResponse = new AjaxResponse();
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)
                ||request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
            if (exception instanceof LockedException) {
                ajaxResponse.error().setMessage("账户已锁定，请联系管理员！");
            } else if (exception instanceof CredentialsExpiredException) {
                ajaxResponse.error().setMessage("密码已过期，请联系管理员！");
            } else if (exception instanceof AccountExpiredException) {
                ajaxResponse.error().setMessage("账户已过期，请联系管理员！");
            } else if (exception instanceof DisabledException) {
                ajaxResponse.error().setMessage("账户已被禁用，请联系管理员!");
            } else if (exception instanceof BadCredentialsException) {
                ajaxResponse.error().setMessage("用户名或密码错误，请重新输入！");
            }
            try {
                ajaxResponse.setCode(403);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(ajaxResponse));
                response.getWriter().flush();
                response.getWriter().close();
            } catch (AuthenticationException | IOException e) {
                e.printStackTrace();
            }

        }else {
            //form 表单数据则使用默认
            super.onAuthenticationFailure(request,response,exception);
        }


    }
}
