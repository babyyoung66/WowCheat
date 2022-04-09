package com.cinle.wowcheat.Service;

import cn.hutool.core.util.RandomUtil;
import com.cinle.wowcheat.Constants.MyConst;
import com.cinle.wowcheat.Enum.MailTypeEnum;
import com.cinle.wowcheat.Event.SendMailEvent;
import com.cinle.wowcheat.Exception.CustomerException;
import com.cinle.wowcheat.Vo.MailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @Author JunLe
 * @Time 2022/2/21 13:53
 */
@Service
public class SendMailServices {

    private final Logger logger = LoggerFactory.getLogger(SendMailServices.class);


    /**
     * 获取发送账户
     */
    @Value("${spring.mail.username}")
    private String userName;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ApplicationContext applicationContext;


    @Autowired
    private VerifyService verifyService;

    /**
     * 事件通知形式发送邮件
     * @param to
     * @param subject
     * @param content
     * @param type
     */
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
    public long SendVerifyMail(String to) throws CustomerException {
        String code = RandomUtil.randomNumbers(MyConst.EMAIL_CODE_LENGTH);
        long time = verifyService.getEmailCodeTTL(to);
         long timer = MyConst.CODE_KEEPALIVE_TIME - MyConst.EMAIL_CODE_WAIT_TIME;
        if (time < timer || time <= 0){
            verifyService.setEmailCode(to,code);
            MailMessage mailMessage = new MailMessage();
            mailMessage.setTo(to).setSubject("【WowCheat】").setContent("【WowCheat】注册验证码:" + code + "，此验证码仅用于WowCheat账号注册，请勿将验证码提供给他人！验证码5分钟内有效，请及时完成注册验证。如非本人操作，请忽略该邮件，祝您生活愉快！");
//            ApplicationEvent event = new SendMailEvent(mailMessage);
//            applicationContext.publishEvent(event);
            sendSimpleMail(mailMessage);
            return 0;
        }
        //计算等待时间
        long wait = MyConst.EMAIL_CODE_WAIT_TIME - (MyConst.CODE_KEEPALIVE_TIME - time);
        return  wait;

    }


    /**
     * 发送简单邮件
     * 使用springEvent异步发送
     */

    public void sendSimpleMail(MailMessage mailMessage) throws CustomerException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(userName);
        message.setTo(mailMessage.getTo());
        message.setSubject(mailMessage.getSubject());
        message.setText(mailMessage.getContent());
        try {
            mailSender.send(message);
        } catch (Exception e) {
            logger.info("发送邮件给{}失败，原因:{}", mailMessage.getTo(), e.getMessage());
            throw new CustomerException(e.getMessage());
        }

    }

    /**
     * 发送HTML邮件
     */

    public void sendHtmlMail(MailMessage mailMessage) {
        //使用MimeMessage，MIME协议
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper; //MimeMessageHelper帮助我们设置更丰富的内容
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(userName);
            helper.setTo(mailMessage.getTo());
            helper.setSubject(mailMessage.getSubject());
            helper.setText(mailMessage.getContent(), true); //true开启支持HTML
            mailSender.send(message);

            logger.info("成功发送html邮件给{}", mailMessage.getTo());
        } catch (MessagingException e) {
            logger.error("发送html邮件给{}失败，原因:{}",  mailMessage.getTo(),e.getMessage());
            // e.printStackTrace();

        }
    }

}
