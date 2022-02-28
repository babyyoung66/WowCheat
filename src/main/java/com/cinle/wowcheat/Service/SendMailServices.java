package com.cinle.wowcheat.Service;

import cn.hutool.core.util.RandomUtil;
import com.cinle.wowcheat.Constants.MyContans;
import com.cinle.wowcheat.Enum.MailTypeEnum;
import com.cinle.wowcheat.Event.SendMailEvent;
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
    private VerifyService verifyService;

    public void sendMail(String to, String subject, String content, MailTypeEnum type){
        MailMessage mailMessage = new MailMessage();
        mailMessage.setTo(to).setSubject(subject).setContent(content).setType(type);
        ApplicationEvent event = new SendMailEvent(mailMessage);
        applicationContext.publishEvent(event);
    }


    /**
     * @param to
     * @return 返回需要等待的时间，返回0则发送成功
     */
    public long SendVerifyMail(String to){
        String code = RandomUtil.randomNumbers(MyContans.EMAIL_CODE_LENGTH);
        long time = verifyService.getEmailCodeTTL(to);
         long timer = MyContans.CODE_KEEPALIVE_TIME - MyContans.EMAIL_CODE_WAIT_TIME;
        if (time < timer || time <= 0){
            verifyService.setEmailCode(to,code);
            MailMessage mailMessage = new MailMessage();
            mailMessage.setTo(to).setSubject("WowCheat").setContent("感谢您的注册!以下是您的验证码:" + code + "(有效期5分钟)。");
            ApplicationEvent event = new SendMailEvent(mailMessage);
            applicationContext.publishEvent(event);
            return 0;
        }
        //计算等待时间
        long wait = MyContans.EMAIL_CODE_WAIT_TIME - (MyContans.CODE_KEEPALIVE_TIME - time);
        return  wait;

    }

}
