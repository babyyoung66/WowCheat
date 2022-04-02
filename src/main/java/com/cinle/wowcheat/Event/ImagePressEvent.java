package com.cinle.wowcheat.Event;

import com.cinle.wowcheat.Model.UserFileDetail;
import org.springframework.context.ApplicationEvent;

/**
 * @Author JunLe
 * @Time 2022/3/29 23:18
 * 图片压缩事件
 */
public class ImagePressEvent extends ApplicationEvent {

    public ImagePressEvent(UserFileDetail source) {
        super(source);
    }
}
