package com.cinle.wowcheat.Security;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Service.MessageServices;
import com.cinle.wowcheat.Service.UserServices;
import com.cinle.wowcheat.Utils.IpUtils;
import com.cinle.wowcheat.Vo.AjaxResponse;
import com.cinle.wowcheat.WebSocket.SendSocketMessageServices;
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
    @Autowired
    SendSocketMessageServices socketMessageServices;
    @Autowired
    UserServices userServices;

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

            String token = null;
            try {
                token = jwtTokenService.createToken(userDetail.getUuid(),roles);
            } catch (Exception e) {
                log.warn("用户验证成功后出现异常，请尝试重新登录！原因: " + e.getMessage());
                e.printStackTrace();
                response.getWriter().write(JSON.toJSONString(ajaxResponse.error().setMessage(e.getMessage())));
                response.getWriter().flush();
                response.getWriter().close();
                return;
            }
            Map map = new HashMap();
            map.put("token",token);
            map.put("user",userDetail);
            ajaxResponse.success().setMessage("登录成功!").setData(map);
            response.getWriter().write(JSON.toJSONString(ajaxResponse));
            response.getWriter().flush();
            response.getWriter().close();
            MyUserDetail user = userServices.selectByWowId(userDetail.getWowId());
            //首次登录时，发送欢迎消息
            if (user.getLastLoginTime() == null){
                socketMessageServices.sendWelcomeMessage(user);
            }
            //更新登录信息
            MyUserDetail update = new MyUserDetail();
            update.setUuid(user.getUuid());
            update.setLastLoginTime(new Date());
            update.setLastLoginIp(IpUtils.getRealIp(request));
            userServices.updateByUUIDSelective(update);
            log.info("用户uuid: {} 已从主机: {}登录服务......",userDetail.getUuid(), IpUtils.getRealIp(request));
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }

    }
}
