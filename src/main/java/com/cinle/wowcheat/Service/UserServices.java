package com.cinle.wowcheat.Service;

import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Security.CustomerUserDetails;

import java.util.List;

public interface UserServices {
    int deleteByUUID(String uuid);


    int insertSelective(MyUserDetail record);

    MyUserDetail selectByPrimaryKey(Integer autoId);

    int updateByUUIDSelective(MyUserDetail record);


    CustomerUserDetails findForLogin(String wowId);


    /**
     * @param record 可以单独传入id或单独传入email，两个都传时则使用or查询
     * @return 返回一个用户列表
     */
    List<MyUserDetail> selectByWowIdOrEmail(MyUserDetail record);
}
