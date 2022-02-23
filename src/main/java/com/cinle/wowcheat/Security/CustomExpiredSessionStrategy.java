package com.cinle.wowcheat.Security;

import com.cinle.wowcheat.Vo.AjaxResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @Author JunLe
 * @Time 2022/2/23 10:22
 * session出现异常处理
 */
public class CustomExpiredSessionStrategy implements SessionInformationExpiredStrategy {
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        AjaxResponse ajaxResponse = new AjaxResponse().error();
        ajaxResponse.setCode(403);
        ajaxResponse.setMessage("登录超时或已在另一台设备登录,您已被强制下线!"
                + event.getSessionInformation().getLastRequest());

        String json = objectMapper.writeValueAsString(ajaxResponse);
        event.getResponse().setContentType("application/json;charset=utf-8");
        event.getResponse().getWriter().write(json);//返回json数据
    }
}
