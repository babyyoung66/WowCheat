package com.cinle.wowcheat.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author JunLe
 * @Time 2022/2/20 19:07
 * 自定义security获取用户身份
 * 可以在这里加上
 * 改写成处理JSON，并使用token方式
 */
public class CustomerUsernamePasswordFilter extends UsernamePasswordAuthenticationFilter {
    private Logger log = LoggerFactory.getLogger(CustomerUsernamePasswordFilter.class);

    private AuthenticationManager authenticationManager;


    /**
     * 认证开始前，
     * 从request获取用户名和密码
     * 也可以在该位置使用request.getParameter()获取验证码进行验证
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if(!request.getMethod().equals("POST")){
            throw new AuthenticationServiceException("Authentication method not supported" + request.getMethod());
        }

        //说明是以json的形式传递参数
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
            ObjectMapper mapper = new ObjectMapper();
            UsernamePasswordAuthenticationToken authRequest = null;
            try (InputStream is = request.getInputStream()){
                CustomerUserDetails customerUserDetails = mapper.readValue(is, CustomerUserDetails.class);
                authRequest = new UsernamePasswordAuthenticationToken(
                        customerUserDetails.getUsername(), customerUserDetails.getPassword());
                // request.getInputStream()流读取一次就清空了
                // 为了防止之后会频繁的使用表单中的参数，一次性全部将表单内容写入到attribute中去
                request.setAttribute("wowId", customerUserDetails.getWowId());
                request.setAttribute("password", customerUserDetails.getPassword());
                request.setAttribute("verifyCode", customerUserDetails.verifyCode);
                request.setAttribute("rememberMe", customerUserDetails.isRememberMe());

            }catch (IOException e) {
                e.printStackTrace();
                log.info("登录输入流转换异常！");
                authRequest = new UsernamePasswordAuthenticationToken(
                        "", "");
            }finally {
                setDetails(request, authRequest);
                return this.getAuthenticationManager().authenticate(authRequest);
            }
        }

        return super.attemptAuthentication(request, response);
    }

//    /**
//     * 验证成功时
//     * */
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        super.successfulAuthentication(request, response, chain, authResult);
//    }
//
//    /**
//     * 验证失败时
//     * */
//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
//        super.unsuccessfulAuthentication(request, response, failed);
//    }
}
