package com.cinle.wowcheat.Event;

import com.cinle.wowcheat.Vo.MailMessage;
import org.springframework.context.ApplicationEvent;

/**
 * @Author JunLe
 * @Time 2022/2/21 13:36
 * 邮件发送event
 */
public class SendMailEvent extends ApplicationEvent {
    public SendMailEvent(MailMessage source) {
        super(source);
    }
}
