package com.cinle.wowcheat.Dao;

import com.cinle.wowcheat.Model.UserFileDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserFileDetailDao {
    int deleteByUUID(UserFileDetail key);

    int insert(UserFileDetail record);

    int insertSelective(UserFileDetail record);

    UserFileDetail selectByUUID(UserFileDetail key);

    int updateByUUIDSelective(UserFileDetail record);

}