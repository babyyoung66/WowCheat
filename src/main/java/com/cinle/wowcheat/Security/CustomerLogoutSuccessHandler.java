package com.cinle.wowcheat.Security;

import com.cinle.wowcheat.Vo.AjaxResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @Author JunLe
 * @Time 2022/2/24 17:56
 */
@Component
public class CustomerLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    JwtTokenService jwtTokenService;

    private Logger log = LoggerFactory.getLogger(CustomerLogoutSuccessHandler.class);

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String token = request.getHeader("token");
            if (StringUtils.hasText(token)) {
                Map info = jwtTokenService.getUserInfoFromToken(token);
                String uuid = (String) info.get("username");
                /*清除redis缓存*/

                AjaxResponse ajaxResponse = new AjaxResponse().success().setMessage("注销成功！").setCode(401);
                //MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal(); //换成token验证，弃用
                PrintWriter out = response.getWriter();
                out.write(String.valueOf(ajaxResponse));
                out.flush();
                out.close();
                log.info("用户uuid: {} 已从主机: {}:{} 退出登录......", uuid, request.getRemoteHost(), request.getRemotePort());
            }else {
                throw new  NullPointerException("无效退出请求......");
            }

        } catch (Exception e) {
            log.info("来着主机: {}:{} 的无效退出请求......", request.getRemoteHost(), request.getRemotePort());
        }

    }
}
