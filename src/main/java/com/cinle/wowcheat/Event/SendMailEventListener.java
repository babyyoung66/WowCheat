package com.cinle.wowcheat.Event;

import com.cinle.wowcheat.Enum.MailTypeEnum;
import com.cinle.wowcheat.Tools.SendMailUtils;
import com.cinle.wowcheat.Vo.MailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Author JunLe
 * @Time 2022/2/21 14:17
 * 使用springEvent时间发布异步发送邮件
 */
@Component
public class SendMailEventListener {

    @Autowired
    SendMailUtils mailComponent;

    @Async("AsyncExecutor")//异步处理
    @EventListener(SendMailEvent.class)
    public void sendMail(SendMailEvent event){
        MailMessage mailMessage = (MailMessage) event.getSource();
        if (mailMessage.getType() == MailTypeEnum.HTML){
            mailComponent.sendHtmlMail(mailMessage);
        }
        mailComponent.sendSimpleMail(mailMessage);
    }
}
