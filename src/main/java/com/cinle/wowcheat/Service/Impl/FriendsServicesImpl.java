package com.cinle.wowcheat.Service.Impl;

import com.cinle.wowcheat.Dao.FriendsDao;
import com.cinle.wowcheat.Model.Friends;
import com.cinle.wowcheat.Service.FriendsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/2/28 15:14
 */
@Service
public class FriendsServicesImpl implements FriendsServices {

    @Autowired
    FriendsDao friendsDao;

    @Override
    public int deleteByUuid(String sUuid, String fUuid) {
        return friendsDao.deleteByUuid(sUuid, fUuid);
    }

    @Override
    public int insertSelective(Friends record) {
        return friendsDao.insertSelective(record);
    }

    @Override
    public int insertByUuid(String sUuid, String fUuid) {
        return friendsDao.insertByUuid(sUuid, fUuid);
    }

    @Override
    public Friends selectByPrimaryKey(Integer autoId) {
        return friendsDao.selectByPrimaryKey(autoId);
    }

    @Override
    public int updateStatusByUuid(Friends record) {
        return friendsDao.updateStatusByUuid(record);
    }

    @Override
    public List<String> selectFriendUuidList(String sUuid) {
        return friendsDao.selectFriendUuidList(sUuid);
    }

    @Override
    public Friends findFriend(String sUuid, String fUuid) {
        return friendsDao.findFriend(sUuid, fUuid);
    }
}
