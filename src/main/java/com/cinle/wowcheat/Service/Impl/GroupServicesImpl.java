package com.cinle.wowcheat.Service.Impl;

import cn.hutool.core.util.IdUtil;
import com.cinle.wowcheat.Dao.GroupDao;
import com.cinle.wowcheat.Model.Group;
import com.cinle.wowcheat.Service.GroupServices;
import com.cinle.wowcheat.Utils.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/4/17 23:25
 */
@Service
public class GroupServicesImpl implements GroupServices {


    @Autowired
    private GroupDao groupDao;

    @Override
    public int deleteByPrimaryKey(Integer autoId) {
        return groupDao.deleteByPrimaryKey(autoId);
    }

    @Override
    public Group insert(Group record) {
        record.setCreateTime(new Date());
        String uuid = IdUtil.objectId();
        record.setUuid(uuid);
        record.setCreatorUuid(SecurityContextUtils.getCurrentUserUUID());
        int res = groupDao.insert(record);
        if (res >0){
            return record;
        }
        return null;
    }

    @Override
    public int insertSelective(Group record) {
        record.setCreateTime(new Date());
        return groupDao.insertSelective(record);
    }

    @Override
    public Group selectByPrimaryKey(Integer autoId) {
        return groupDao.selectByPrimaryKey(autoId);
    }

    @Override
    public Group selectByUuid(String uuid) {
        return groupDao.selectByUuid(uuid);
    }

    @Override
    public List<Group> selectGroupsByUserUuid(String userUuid) {
        return groupDao.selectGroupsByUserUuid(userUuid);
    }

    @Override
    public int updateByUuidSelective(Group group) {
        return groupDao.updateByUuidSelective(group);
    }


    @Override
    public int updateGroupStatusByUuid(String uuid, int groupStatus) {
        return groupDao.updateGroupStatusByUuid(uuid, groupStatus);
    }
}
