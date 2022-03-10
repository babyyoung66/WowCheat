package com.cinle.wowcheat.Service;

import com.cinle.wowcheat.Constants.MessageConst;
import com.cinle.wowcheat.Model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/3/4 15:57
 */
@Service
public class MessageServices {
    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * @return 返回收发双方全部消息
     */
    public List<Message> findAllByUUID(Message message,String collectionName){
        //or查询,只查询180天内的
        Query query = new Query(new Criteria().orOperator(
                Criteria.where("from").is(message.getFrom())
                .and("to").is(message.getTo()),
                Criteria.where("from").is(message.getTo())
                .and("to").is(message.getFrom())

        ).and("time").gte(getQueryStartTime()) //筛选时间
        );
        return mongoTemplate.find(query,Message.class,collectionName);
    }

    /**
     * @return 获取当前日期x天之前的时间
     */
    private Date getQueryStartTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, - MessageConst.Message_Expiration);
        return calendar.getTime();
    }

    /**
     * @param message
     * @param collectionName 集合名称
     * @return
     * 传入前一次查询的最早的一条记录
     * 以该条记录的时间作为查询的结束范围
     */
    public List<Message> findPersonalByPages(Message message,String collectionName) {
        Date date;
        //时间为空则默认当前时间
        if (message.getTime() ==null || message.getTime().equals("")){
            date = new Date();
        }else {
            date = message.getTime(); //将日期格式转换为系统可识别的
        }
        Query query = new Query(
                new Criteria().orOperator(
                        Criteria.where("from").is(message.getFrom())
                                .and("to").is(message.getTo()),
                        Criteria.where("from").is(message.getTo())
                                .and("to").is(message.getFrom())
                ).and("time").gte(getQueryStartTime()).lte(date)
        );
        query.with(Sort.by(Sort.Order.desc("time"))).limit(MessageConst.Page_Num);//按时间降序取前40条
        List<Message> list = mongoTemplate.find(query,Message.class,collectionName);
        Collections.reverse(list);//由于是降序查询，需要反转回降序给前端
        return list;
    }

    public int saveMessage(Message message,String collectionName){
        Date now = new Date();
        message.setTime(now);
        //默认未读状态
        message.setCheck(false);
        Message result = mongoTemplate.save(message,collectionName);
        return result==null?0:1;

    }

    public void delOne(String _id,String collectionName){
        Query query = new Query(Criteria.where("_id").is(_id));
        mongoTemplate.remove(_id,collectionName);
    }

    public List<Message> findGroupByPage(Message message,String collectionName){
        Date date;
        //时间为空则默认当前时间
        if (message.getTime() ==null || message.getTime().equals("")){
            date = new Date();
        }else {
            date = message.getTime(); //将日期格式转换为系统可识别的
        }
        Query query = new Query(
                        Criteria.where("to").is(message.getTo())
                        .and("time").gte(getQueryStartTime()).lte(date)
        );
        query.with(Sort.by(Sort.Order.desc("time"))).limit(MessageConst.Page_Num*2);//按时间降序取前80条
        List<Message> list = mongoTemplate.find(query,Message.class,collectionName);
        Collections.reverse(list);//由于是降序查询，需要反转回降序给前端
        return list;
    }


}
