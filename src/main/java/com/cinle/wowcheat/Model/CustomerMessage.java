package com.cinle.wowcheat.Model;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.data.annotation.Id;

import java.util.Date;


/**
 * @Author JunLe
 * @Time 2022/3/3 15:26
 */
//@Document(collection = "message") //弃用，使用MongoTemplate自定义选择collection
public class CustomerMessage {

    @Id
    private String _id;

    private String from;

    private String to;

    private String content;

    private String contentType;

    private FileDetail fileDetail;



    //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   @JSONField(format="yyyy-MM-dd HH:mm:ss.SSS")
   //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", locale = "zh", timezone = "GMT+8") //混用会出现bug
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

    public CustomerMessage setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getTo() {
        return to;
    }

    public CustomerMessage setTo(String to) {
        this.to = to;
        return this;
    }

    public String getContent() {
        return content;
    }

    public CustomerMessage setContent(String content) {
        this.content = content;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public CustomerMessage setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }


    public Date getTime() {
        return time;
    }

    public CustomerMessage setTime(Date time) {
        this.time = time;
        return this;
    }

    public String getMsgType() {
        return msgType;
    }

    public CustomerMessage setMsgType(String msgType) {
        this.msgType = msgType;
        return this;
    }

    public FileDetail getFileDetail() {
        return fileDetail;
    }

    public CustomerMessage setFileDetail(FileDetail fileDetail) {
        this.fileDetail = fileDetail;
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
                ", fileDetail=" + fileDetail +
                ", time=" + time +
                ", msgType='" + msgType + '\'' +
                '}';
    }
}