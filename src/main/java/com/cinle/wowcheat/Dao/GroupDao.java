package com.cinle.wowcheat.Dao;

import com.cinle.wowcheat.Model.Group;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface GroupDao {
    int deleteByPrimaryKey(Integer autoId);

    int insert(Group record);

    int insertSelective(Group record);

    Group selectByPrimaryKey(Integer autoId);

    Group selectByUuid(@Param("uuid") String uuid);


    /**
     * 根据用户uuid，获取用户所有相关group,
     * 携带自身关联信息
     */
    List<Group> selectGroupsByUserUuid(@Param("userUuid") String userUuid);



    int updateByUuidSelective(Group group);


    int updateGroupStatusByUuid(@Param("uuid") String uuid,@Param("groupStatus") int groupStatus);



}