package com.cinle.wowcheat.Dao;

import com.cinle.wowcheat.Model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/3/4 15:54
 * mongo查询接口
 */
@Component
public interface MessageDao extends MongoRepository<Message,String> {
     @Query(value = "{'from':?0,'to':1}")
     List<Message> findByFromAndTo(String from,String to);
}
