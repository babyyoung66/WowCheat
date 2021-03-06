package com.cinle.wowcheat.Service.Impl;

import cn.hutool.core.util.IdUtil;
import com.cinle.wowcheat.AOP.TestUserForbidden;
import com.cinle.wowcheat.Dao.UserdetailDao;
import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Security.CustomerUserDetails;
import com.cinle.wowcheat.Service.UserServices;
import com.cinle.wowcheat.Utils.DesensitizedUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/2/20 18:37
 */
@Service
public class UserServiceImpl implements UserServices {

    @Autowired
    UserdetailDao userdetailDao;


    @Override
    public int deleteByUUID(String uuid) {
        return userdetailDao.deleteByUUID(uuid);
    }

    @Override
    public MyUserDetail insertSelective(MyUserDetail record) {
        BCryptPasswordEncoder bcry = new BCryptPasswordEncoder();
        record.setPassword(bcry.encode(record.getPassword()));
        /*使用hutool工具生成uuid*/
        String uuid = IdUtil.objectId();
        //记录插入时间
        Date dateTime = new Date();
        record.setCreateTime(dateTime);
        record.setUuid(uuid);
        int res = userdetailDao.insertSelective(record);
        if (res > 0){
            return record;
        }
        return null;
    }

    @Override
    public MyUserDetail selectByPrimaryKey(Integer autoId) {
        return userdetailDao.selectByPrimaryKey(autoId);
    }

    @Override
    public int updateByUUIDSelective(MyUserDetail record) {
        BCryptPasswordEncoder bcry = new BCryptPasswordEncoder();
        if (StringUtils.hasText(record.getPassword())){
            record.setPassword(bcry.encode(record.getPassword()));
        }
        return userdetailDao.updateByUUIDSelective(record);
    }


    @Override
    public int updatePassWordByUUID(MyUserDetail record) {
        BCryptPasswordEncoder bcry = new BCryptPasswordEncoder();
        if (StringUtils.hasText(record.getPassword())){
            record.setPassword(bcry.encode(record.getPassword()));
        }
        return userdetailDao.updateByUUIDSelective(record);
    }


    @Override
    public CustomerUserDetails findForLogin(String wowId) {
        return userdetailDao.findForLogin(wowId);
    }

    @Override
    public List<MyUserDetail> selectByWowIdOrEmail(MyUserDetail record) {
        return userdetailDao.selectByWowIdOrEmail(record);
    }

    @Override
    public List<MyUserDetail> selectByFriendsUuidList(List<String> uuidList, String sUuid) {
        if (uuidList == null || uuidList.size() == 0){
            return new ArrayList<>();
        }
        //脱敏工作
        List<MyUserDetail> users = userdetailDao.selectByFriendsUuidList(uuidList, sUuid);
        List<MyUserDetail> list = new ArrayList<>();
        if (users != null && !users.isEmpty()) {
            users.forEach(u -> {
                u.setEmail(DesensitizedUtil.email(u.getEmail()));
                u.setPhonenum(DesensitizedUtil.mobilePhone(u.getPhonenum()));
                list.add(u);
            });
        }
        return list;
    }

    @Override
    public List<MyUserDetail> selectUsersByUUIDs(List<String> uuidList) {
        if (uuidList == null || uuidList.size() == 0){
            return new ArrayList<>();
        }
        return userdetailDao.selectUsersByUUIDs(uuidList);
    }

    @Override
    public MyUserDetail selectByFriendUuid(String sUuid, String fUuid) {
        return userdetailDao.selectByFriendUuid(sUuid,fUuid);
    }

    @Override
    public MyUserDetail selectByUUID(String uuid) {
        MyUserDetail usr = userdetailDao.selectByUUID(uuid);
        if (usr != null) {
            usr.setEmail(DesensitizedUtil.email(usr.getEmail()));
            usr.setPhonenum(DesensitizedUtil.mobilePhone(usr.getPhonenum()));
        }
        return usr;
    }

    @Override
    public MyUserDetail selectByWowId(String wowId) {
        MyUserDetail usr = userdetailDao.selectByWowId(wowId);
        if (usr != null) {
            usr.setPhonenum(DesensitizedUtil.mobilePhone(usr.getPhonenum()));
            usr.setEmail(DesensitizedUtil.email(usr.getEmail()));
        }
        return usr;
    }
}
