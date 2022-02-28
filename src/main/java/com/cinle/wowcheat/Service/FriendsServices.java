package com.cinle.wowcheat.Service;

import com.cinle.wowcheat.Model.Friends;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/2/28 15:12
 */
public interface FriendsServices {
    int deleteByUuid(String sUuid , String fUuid);

    int insertSelective(Friends record);

    int insertByUuid( String sUuid ,  String fUuid);

    Friends selectByPrimaryKey(Integer autoId);

    int updateStatusByUuid(Friends record);

    List<String> selectFriendUuidList(String sUuid);

    Friends findFriend(String sUuid ,  String fUuid);
}
