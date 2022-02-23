package com.cinle.wowcheat.Service.Impl;

import com.cinle.wowcheat.Dao.RoleDao;
import com.cinle.wowcheat.Model.Role;
import com.cinle.wowcheat.Service.RoleServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/2/21 9:44
 */
@Service
public class RoleServicesImpl implements RoleServices {

    @Autowired
    RoleDao roleDao;
    @Override
    public List<Role> selectByUseruid(String uuid) {
        return roleDao.selectByUseruid(uuid);
    }
}
