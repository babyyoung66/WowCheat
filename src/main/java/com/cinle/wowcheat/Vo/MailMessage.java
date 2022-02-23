package com.cinle.wowcheat.Vo;

import com.cinle.wowcheat.Enum.MailTypeEnum;

/**
 * @Author JunLe
 * @Time 2022/2/16 10:59
 */
public class MailMessage {
    private String form;
    private String to;
    private String subject;
    private String content;
    private MailTypeEnum type;

    public String getForm() {
        return form;
    }

    public MailMessage setForm(String form) {
        this.form = form;
        return this;
    }

    public String getTo() {
        return to;
    }

    public MailMessage setTo(String to) {
        this.to = to;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public MailMessage setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getContent() {
        return content;
    }

    public MailMessage setContent(String content) {
        this.content = content;
        return this;
    }

    public MailTypeEnum getType() {
        return type;
    }

    public MailMessage setType(MailTypeEnum type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return "MailMessage{" +
                "form='" + form + '\'' +
                ", to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                '}';
    }
}
