package com.cinle.wowcheat.Dao;

import com.cinle.wowcheat.Model.FriendsRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


@Component
@Mapper
public interface FriendsRequestDao {
    int deleteByPrimaryKey(Integer autoId);

    int insertSelective(FriendsRequest record);

    FriendsRequest selectByPrimaryKey(Integer autoId);

    int updateByPrimaryKeySelective(FriendsRequest record);

    List<FriendsRequest> selectListWithUserInfoByShelfUuid(@Param("Uuid") String Uuid, @Param("startTime") Date startTime);

    /**
     * 查询请求信息
     * infoUuid 要查询的userinfo的信息
     */
    FriendsRequest selectWithUserInfoByEachUuid(@Param("requestUuid") String requestUuid, @Param("receiverUuid") String receiverUuid,@Param("infoUuid") String infoUuid);

    int updateRequestStatusByUuid(FriendsRequest request);

    FriendsRequest selectByRequestAndReceiverUuid(FriendsRequest request);
}