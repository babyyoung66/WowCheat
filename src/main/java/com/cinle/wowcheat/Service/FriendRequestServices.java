package com.cinle.wowcheat.Service;

import com.cinle.wowcheat.Model.FriendsRequest;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/4/5 23:56
 */
public interface FriendRequestServices {
    int deleteByPrimaryKey(Integer autoId);

    int insertSelective(FriendsRequest record);

    FriendsRequest selectByPrimaryKey(Integer autoId);

    int updateByPrimaryKeySelective(FriendsRequest record);

    List<FriendsRequest> selectListWithUserInfoByShelfUuid(String Uuid);

    FriendsRequest selectWithUserInfoByEachUuid(String requestUuid, String receiverUuid,String infoUuid);

    int updateRequestStatusByUuid(FriendsRequest record);

    FriendsRequest selectByRequestAndReceiverUuid(FriendsRequest request);
}
