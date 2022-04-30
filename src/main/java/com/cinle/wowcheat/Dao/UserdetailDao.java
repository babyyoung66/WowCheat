package com.cinle.wowcheat.Dao;

import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Security.CustomerUserDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserdetailDao {
    int deleteByUUID(String autoId);

    int insertSelective(MyUserDetail record);

    MyUserDetail selectByPrimaryKey(Integer autoId);

    int updateByUUIDSelective(MyUserDetail record);

    CustomerUserDetails findForLogin(String wowId);

    List<MyUserDetail> selectByWowIdOrEmail(MyUserDetail record);

    List<MyUserDetail> selectByFriendsUuidList(@Param("uuidList") List<String> uuidList ,@Param("sUuid") String sUuid );

    /**
     * 不包含隐私信息
     */
    List<MyUserDetail> selectUsersByUUIDs(@Param("uuidList") List<String> uuidList );

    MyUserDetail selectByFriendUuid(@Param("sUuid") String sUuid,@Param("fUuid") String fUuid);

    MyUserDetail selectByUUID(@Param("uuid")String uuid);

    MyUserDetail selectByWowId(@Param("wowId") String wowId);

}