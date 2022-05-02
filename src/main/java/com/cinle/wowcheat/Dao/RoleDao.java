package com.cinle.wowcheat.Dao;

import com.cinle.wowcheat.Enum.RoleEnum;
import com.cinle.wowcheat.Model.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface RoleDao {
    int deleteByPrimaryKey(Integer autoId);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Integer autoId);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

    List<Role> selectByUseruid(String uuid);

    List<String> selectUserIdsByRoleType(@Param("roleType") RoleEnum roleType);
}