package com.cinle.wowcheat.Model;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.data.annotation.Id;

import java.util.Date;


/**
 * @Author JunLe
 * @Time 2022/3/3 15:26
 */
//@Document(collection = "message") //弃用，使用MongoTemplate自定义选择collection
public class CheatMessage {

    @Id
    private String _id;

    private String from;

    private String to;

    private String content;

    private String contentType;

    private FileDetail fileDetail;


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

    public CheatMessage setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getTo() {
        return to;
    }

    public CheatMessage setTo(String to) {
        this.to = to;
        return this;
    }

    public String getContent() {
        return content;
    }

    public CheatMessage setContent(String content) {
        this.content = content;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public CheatMessage setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }


    public Date getTime() {
        return time;
    }

    public CheatMessage setTime(Date time) {
        this.time = time;
        return this;
    }

    public String getMsgType() {
        return msgType;
    }

    public CheatMessage setMsgType(String msgType) {
        this.msgType = msgType;
        return this;
    }

    public FileDetail getFileDetail() {
        return fileDetail;
    }

    public CheatMessage setFileDetail(FileDetail fileDetail) {
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
