package com.cinle.wowcheat.Service;

import cn.hutool.core.util.RandomUtil;
import com.cinle.wowcheat.Enum.MailTypeEnum;
import com.cinle.wowcheat.Event.SendMailEvent;
import com.cinle.wowcheat.Utils.VerifyUtils;
import com.cinle.wowcheat.Vo.MailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;

/**
 * @Author JunLe
 * @Time 2022/2/21 13:53
 */
@Service
public class SendMailServices {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private VerifyUtils verifyUtils;

    public void sendMail(String to, String subject, String content, MailTypeEnum type){
        MailMessage mailMessage = new MailMessage();
        mailMessage.setTo(to).setSubject(subject).setContent(content).setType(type);
        ApplicationEvent event = new SendMailEvent(mailMessage);
        applicationContext.publishEvent(event);
    }


    /**
     * @param to
     * @return 1成功发送,-1 等待60s
     */
    public int SendVerifyMail(String to){
        String code = RandomUtil.randomNumbers(6);
        long time = verifyUtils.getEmailCodeTTL(to);
        if (time < 240 || time < 0){
            verifyUtils.setEmailCode(to,code);
            MailMessage mailMessage = new MailMessage();
            mailMessage.setTo(to).setSubject("WowCheat").setContent("感谢您的注册!以下是您的验证码:" + code + "(有效期5分钟)。");
            ApplicationEvent event = new SendMailEvent(mailMessage);
            applicationContext.publishEvent(event);
            return 1;
        }

        return -1;

        /* 后续加再入redis方便验证*/
    }

}
