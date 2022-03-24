package com.cinle.wowcheat.Service;

import cn.hutool.core.util.RandomUtil;
import com.cinle.wowcheat.Constants.MyConst;
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
        String code = RandomUtil.randomNumbers(MyConst.EMAIL_CODE_LENGTH);
        long time = verifyService.getEmailCodeTTL(to);
         long timer = MyConst.CODE_KEEPALIVE_TIME - MyConst.EMAIL_CODE_WAIT_TIME;
        if (time < timer || time <= 0){
            verifyService.setEmailCode(to,code);
            MailMessage mailMessage = new MailMessage();
            mailMessage.setTo(to).setSubject("【WowCheat】").setContent("【WowCheat】注册验证码:" + code + "，此验证码仅用于WowCheat账号注册，请勿将验证码提供给他人！验证码5分钟内有效，请及时完成注册验证。如非本人操作，请忽略该邮件，祝您生活愉快！");
            ApplicationEvent event = new SendMailEvent(mailMessage);
            applicationContext.publishEvent(event);
            return 0;
        }
        //计算等待时间
        long wait = MyConst.EMAIL_CODE_WAIT_TIME - (MyConst.CODE_KEEPALIVE_TIME - time);
        return  wait;

    }

}
