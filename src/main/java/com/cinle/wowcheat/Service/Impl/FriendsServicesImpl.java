package com.cinle.wowcheat.Service.Impl;

import com.cinle.wowcheat.Dao.FriendsDao;
import com.cinle.wowcheat.Model.Friends;
import com.cinle.wowcheat.Service.FriendsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
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
        LocalDateTime now = LocalDateTime.now();
        record.setLastCheatTime(now);
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
    public int updateStatusByUuid(String sUuid ,  String fUuid,Integer status) {
        return friendsDao.updateStatusByUuid(sUuid, fUuid,status);
    }

    @Override
    public List<String> selectFriendUuidList(String sUuid) {
        return friendsDao.selectFriendUuidList(sUuid);
    }

    @Override
    public Friends findFriend(String sUuid, String fUuid) {
        return friendsDao.findFriend(sUuid, fUuid);
    }

    @Override
    public int updateRemarksByUuid(Friends friends) {
        return friendsDao.updateRemarksByUuid(friends);
    }

    @Override
    public int updateLastCheatTime(Friends record) {
        LocalDateTime now = LocalDateTime.now();
        record.setLastCheatTime(now);
        return friendsDao.updateLastCheatTime(record);
    }
}
