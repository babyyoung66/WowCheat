package com.cinle.wowcheat.Model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;

import java.util.Date;


/**
 * @Author JunLe
 * @Time 2022/3/3 15:26
 */
//@Document(collection = "message") //弃用，使用MongoTemplate自定义选择collection
public class Message {

    @Id
    private String _id;

    private String from;

    private String to;

    private String content;

    private String contentType;

    /**
     * 是否读取默认false
     */
    private boolean check;

    //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format="yyyy-MM-dd HH:mm:ss",deserialize=false) //关闭反序列化，不然无法转换类型，使用下边的转换
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date time;

    private String msgType;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFrom() {
        return from;
    }

    public Message setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getTo() {
        return to;
    }

    public Message setTo(String to) {
        this.to = to;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Message setContent(String content) {
        this.content = content;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public Message setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public Date getTime() {
        return time;
    }

    public Message setTime(Date time) {
        this.time = time;
        return this;
    }

    public String getMsgType() {
        return msgType;
    }

    public Message setMsgType(String msgType) {
        this.msgType = msgType;
        return this;
    }

    @Override
    public String toString() {
        return "Message{" +
                "_id='" + _id + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", content='" + content + '\'' +
                ", contentType='" + contentType + '\'' +
                ", check=" + check +
                ", time=" + time +
                ", msgType='" + msgType + '\'' +
                '}';
    }
}
