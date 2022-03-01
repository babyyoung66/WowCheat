package com.cinle.wowcheat.Event;

import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Service.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Author JunLe
 * @Time 2022/2/28 22:49
 * 用户修改密码或其他权限相关信息时
 * 发布UserChangeEvent事件，清除token
 */
@Component
public class UserChangeEventListener  {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Async
    @EventListener(UserChangeEvent.class)
    public void removeTokenOnRedis(UserChangeEvent event){
        MyUserDetail user = (MyUserDetail) event.getSource();
        jwtTokenService.RemoveTokenOnRedis(user.getUuid());
    }
}
