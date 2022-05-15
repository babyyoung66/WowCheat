package com.cinle.wowcheat.Security;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Utils.IpUtils;
import com.cinle.wowcheat.Vo.AjaxResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author JunLe
 * @Time 2022/2/24 18:59
 * 重写匿名访问返回内容
 */
public class CustomerAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private Logger log = LoggerFactory.getLogger(CustomerAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        AjaxResponse ajaxResponse = new AjaxResponse().error().setMessage("不允许匿名访问，请先登录系统！").setCode(401);
        PrintWriter out = response.getWriter();
        out.write(JSON.toJSONString(ajaxResponse));
        out.flush();
        out.close();
        //log.info("来自主机: {}的匿名访问，访问路径为: {}", IpUtils.getRealIp(request),request.getRequestURI());
        return;
    }
}
