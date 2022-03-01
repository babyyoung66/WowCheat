package com.cinle.wowcheat.Event;

import com.cinle.wowcheat.Model.MyUserDetail;
import org.springframework.context.ApplicationEvent;

/**
 * @Author JunLe
 * @Time 2022/2/28 22:24
 * user更改信息时，发出通知
 * 更新token或者强制下线
 */
public class UserChangeEvent extends ApplicationEvent {

    public UserChangeEvent(MyUserDetail source) {
        super(source);
    }
}
