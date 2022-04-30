package com.cinle.wowcheat.Service;

import com.cinle.wowcheat.Model.Group;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/4/17 19:32
 */
public interface GroupServices {
    int deleteByPrimaryKey(Integer autoId);

    Group insert(Group record);

    int insertSelective(Group record);

    Group selectByPrimaryKey(Integer autoId);

    Group selectByUuid( String uuid);

    /**
     * 根据用户uuid，获取用户所有相关group
     */
    List<Group> selectGroupsByUserUuid( String userUuid);

    int updateByUuidSelective(Group group);

    int updateGroupStatusByUuid(String uuid, int groupStatus);
}
