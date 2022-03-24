package com.cinle.wowcheat.Utils;

import com.cinle.wowcheat.Vo.MailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * @Author JunLe
 * @Time 2022/2/16 10:11
 */
@Component
public class SendMailUtils {
    private final Logger logger = LoggerFactory.getLogger(SendMailUtils.class);


    /**
     * 获取发送账户
     */
    @Value("${spring.mail.username}")
    private String userName;

    @Autowired
    private JavaMailSender mailSender;


    /**
     * 发送简单邮件
     * 使用springEvent异步发送
     */

    public void sendSimpleMail(MailMessage mailMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(userName);
        message.setTo(mailMessage.getTo());
        message.setSubject(mailMessage.getSubject());
        message.setText(mailMessage.getContent());
        try {
            mailSender.send(message);
            logger.info("成功发送邮件给{}",  mailMessage.getTo());
        } catch (Exception e) {
            logger.info("发送邮件给{}失败，原因:{}", mailMessage.getTo(), e.getMessage());
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
            logger.info("发送html邮件给{}失败，原因:{}",  mailMessage.getTo(),e.getMessage());
           // e.printStackTrace();

        }
    }
}
