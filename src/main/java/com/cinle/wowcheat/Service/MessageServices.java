package com.cinle.wowcheat.Service;

import com.cinle.wowcheat.Constants.MessageConst;
import com.cinle.wowcheat.Model.CheatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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
    public List<CheatMessage> findAllByUUID(CheatMessage cheatMessage, String collectionName) {
        //or查询,只查询180天内的
        Query query = new Query(new Criteria().orOperator(
                Criteria.where("from").is(cheatMessage.getFrom())
                        .and("to").is(cheatMessage.getTo()),
                Criteria.where("from").is(cheatMessage.getTo())
                        .and("to").is(cheatMessage.getFrom())

        ).and("time").gte(getQueryStartTime()) //筛选时间
        );
        return mongoTemplate.find(query, CheatMessage.class, collectionName);
    }

    /**
     * @return 获取允许查询的时间开始范围
     */
    private Date getQueryStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -MessageConst.Message_Expiration);
        return calendar.getTime();
    }

    /**
     * @param cheatMessage
     * @param collectionName  集合名称
     * @return 传入前一次查询的最早的一条记录
     * 以该条记录的时间作为查询的结束范围
     */
    public List<CheatMessage> findMessageByPages(CheatMessage cheatMessage, String collectionName) {
        Date date;
        //时间为空则默认当前时间
        if (cheatMessage.getTime() == null || cheatMessage.getTime().equals("")) {
            date = new Date();
        } else {
            date = cheatMessage.getTime(); //将日期格式转换为系统可识别的
        }
        //转换成Timestamp，直接用date无法精确到毫秒
        Timestamp timestamp = new Timestamp(date.getTime());
        Query query = null;
        //群聊
        if ("group".equals(cheatMessage.getMsgType())) {
            query = new Query(
                    new Criteria().orOperator(
                            Criteria.where("to").is(cheatMessage.getTo())
                    ).and("time").gte(getQueryStartTime()).lt(timestamp)
            );
        }
        if ("personal".equals(cheatMessage.getMsgType())){
            //个人
            query = new Query(
                    new Criteria().orOperator(
                            Criteria.where("from").is(cheatMessage.getFrom())
                                    .and("to").is(cheatMessage.getTo()),
                            Criteria.where("from").is(cheatMessage.getTo())
                                    .and("to").is(cheatMessage.getFrom())
                    ).and("time").gte(getQueryStartTime()).lt(timestamp)
            );
        }
        if (query == null){
            return null;
        }
        query.with(Sort.by(Sort.Order.desc("time"))).limit(MessageConst.Page_Num);//按时间降序取前40条
        List<CheatMessage> list = mongoTemplate.find(query, CheatMessage.class, collectionName);
        Collections.reverse(list);//由于是降序查询，需要反转回降序给前端
        return list;
    }

    public int saveMessage(CheatMessage cheatMessage, String collectionName) {
        Date now = new Date();
        cheatMessage.setTime(now);
        CheatMessage result = mongoTemplate.save(cheatMessage, collectionName);
        return result == null ? 0 : 1;

    }

    public void delOne(String _id, String collectionName) {
        Query query = new Query(Criteria.where("_id").is(_id));
        mongoTemplate.remove(_id, collectionName);
    }

    public List<CheatMessage> findGroupByPage(CheatMessage cheatMessage, String collectionName) {
        Date date;
        //时间为空则默认当前时间
        if (cheatMessage.getTime() == null || cheatMessage.getTime().equals("")) {
            date = new Date();
        } else {
            date = cheatMessage.getTime(); //将日期格式转换为系统可识别的
        }
        //转换成Timestamp，直接用date无法精确到毫秒
        Timestamp timestamp = new Timestamp(date.getTime());
        Query query = new Query(
                Criteria.where("to").is(cheatMessage.getTo())
                        .and("time").gte(getQueryStartTime()).lt(timestamp)
        );
        query.with(Sort.by(Sort.Order.desc("time"))).limit(MessageConst.Page_Num * 2);//按时间降序取前80条
        List<CheatMessage> list = mongoTemplate.find(query, CheatMessage.class, collectionName);
        Collections.reverse(list);//由于是降序查询，需要反转回降序给前端
        return list;
    }

    /**
     * @param fromUuid       消息发送者uuid
     * @param toUuid         消息接收者uuid
     * @param time           上次联系时间
     * @param collectionName
     * @return 返回个人未读记录数
     */
    public long getPersonalUnReadTotal(String fromUuid, String toUuid, Timestamp time, String collectionName) {
        Query query = new Query(
                Criteria.where("to").is(toUuid) //请求者
                        .and("from").is(fromUuid)
                        .and("time").gte(time)
        );
        return mongoTemplate.count(query, collectionName);
    }

    /**
     * @param groupId        群聊id
     * @param time
     * @param collectionName
     * @return 返回未读的群聊记录
     */
    public long getGroupUnReadTotal(String shelfId ,String groupId, Timestamp time, String collectionName) {
        Query query = new Query(
                Criteria.where("to").is(groupId)
                        .and("from").ne(shelfId)
                        .and("time").gte(getQueryStartTime()).gte(time)
        );
        return mongoTemplate.count(query, collectionName);
    }


}
