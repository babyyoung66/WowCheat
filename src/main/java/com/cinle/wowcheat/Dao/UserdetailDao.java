package com.cinle.wowcheat.Dao;

import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Security.CustomerUserDetails;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserdetailDao {
    int deleteByUUID(String autoId);


    int insertSelective(MyUserDetail record);

    MyUserDetail selectByPrimaryKey(Integer autoId);

    int updateByUUIDSelective(MyUserDetail record);


    CustomerUserDetails findForLogin(String wowId);

    List<MyUserDetail> selectByWowIdOrEmail(MyUserDetail record);
}