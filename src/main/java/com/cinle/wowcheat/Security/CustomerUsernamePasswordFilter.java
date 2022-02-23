package com.cinle.wowcheat.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author JunLe
 * @Time 2022/2/20 19:07
 * 自定义security获取用户身份
 * 可以在这里加上
 * 改写成处理JSON
 */
public class CustomerUsernamePasswordFilter extends UsernamePasswordAuthenticationFilter {
    private Logger log = LoggerFactory.getLogger(CustomerUsernamePasswordFilter.class);
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if(!request.getMethod().equals("POST")){
            throw new AuthenticationServiceException("Authentication method not supported" + request.getMethod());
        }
        System.out.println("MyUsernamePasswordAuthenticationFilter");
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
                // 即使多次使用参数我们直接getAttribute就可以拿到参数不用每次都使用流（也获取不到流了，会报流已关闭异常）
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
}
