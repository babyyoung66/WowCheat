package com.cinle.wowcheat.Security;

import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Vo.AjaxResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author JunLe
 * @Time 2022/2/24 17:56
 */
public class CustomerLogoutSuccessHandler implements LogoutSuccessHandler {

    private Logger log = LoggerFactory.getLogger(CustomerLogoutSuccessHandler.class);
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            AjaxResponse ajaxResponse = new AjaxResponse().success().setMessage("注销成功！").setCode(401);
            MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal();
            PrintWriter out = response.getWriter();
            out.write(String.valueOf(ajaxResponse));
            out.flush();
            out.close();
            log.info("用户uuid: {} 已从主机: {}:{} 退出登录......",userDetail.getUuid(),request.getRemoteHost(),request.getRemotePort());
        }catch (NullPointerException e){
            log.info("来着主机: {}:{} 的无效退出请求......",request.getRemoteHost(),request.getRemotePort());
        }

    }
}
