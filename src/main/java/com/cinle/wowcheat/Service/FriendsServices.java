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

    int updateStatusByUuid(String sUuid ,  String fUuid,Integer status);

    List<String> selectFriendUuidList(String sUuid);

    /**
     * 带缓存的查询
     * 用于验证双方身份
     */
    Friends findFriend(String sUuid ,  String fUuid);

    /**
     * 无缓存查询
     * 用于实时查询
     */
    Friends findFriendNonCache(String sUuid, String fUuid);

    int updateRemarksByUuid(Friends friends);

    int updateLastCheatTime(Friends friends);
}
