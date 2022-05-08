package com.cinle.wowcheat.Dao;

import com.cinle.wowcheat.Model.Friends;
import com.cinle.wowcheat.Model.GroupMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Mapper
@Component
public interface FriendsDao {
    int deleteByUuid(@Param("sUuid")String sUuid ,@Param("fUuid") String fUuid);

    int insertSelective(Friends record);


    int insertByUuid(@Param("sUuid") String sUuid , @Param("fUuid") String fUuid);

    Friends selectByPrimaryKey(Integer autoId);

    int updateStatusByUuid(@Param("sUuid")String sUuid ,@Param("fUuid") String fUuid,@Param("fstatus") Integer status);

    List<String> selectFriendUuidList(String sUuid);

    /**
     * 好友视角，请求者的状态
     * */
    List<Friends> selectShelfInfoByFriendIdList(@Param("sUuid") String sUuid,@Param("IdList") List<String> IdList);

    //带缓存
    Friends findFriend(@Param("sUuid")String sUuid ,@Param("fUuid") String fUuid);

    //不带缓存
    Friends findFriendNonCache(@Param("sUuid")String sUuid ,@Param("fUuid") String fUuid);

    int updateRemarksByUuid(Friends friends);

    int updateLastCheatTime(Friends friends);

}