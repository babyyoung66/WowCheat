package com.cinle.wowcheat.Service.Impl;

import com.cinle.wowcheat.Dao.GroupMemberDao;
import com.cinle.wowcheat.Exception.CustomerException;
import com.cinle.wowcheat.Model.GroupMember;
import com.cinle.wowcheat.Redis.GroupMemberCache;
import com.cinle.wowcheat.Service.GroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/4/26 23:08
 */
@Service
public class GroupMemberServiceImpl implements GroupMemberService {

    @Autowired
    GroupMemberDao memberDao;
    @Autowired
    GroupMemberCache groupMemberCache;

    @Override
    public int deleteByPrimaryKey(Integer autoId) {
        return memberDao.deleteByPrimaryKey(autoId);
    }


    @Override
    public int insertSelective(GroupMember record) {
        record.setJoinTime(new Date());
        record.setMemberStatus(0);
        record.setNotifyStatus(0);
        record.setMemberRole(0);
        int res = memberDao.insertSelective(record);
        if (res > 0){
            groupMemberCache.removeAllCache(record.getGroupUuid());
        }
        return res;
    }

    @Override
    public GroupMember selectByPrimaryKey(Integer autoId) {
        return memberDao.selectByPrimaryKey(autoId);
    }

    @Override
    public GroupMember selectByGroupIdAndMemberId(String groupId, String memberId) {
        return memberDao.selectByGroupIdAndMemberId(groupId, memberId);
    }

    @Override
    public int updateByPrimaryKeySelective(GroupMember record) {
        return memberDao.updateByPrimaryKeySelective(record);
    }


    @Override
    public List<String> getMemberIdListByGroupId(String groupId) {
        return memberDao.getMemberIdListByGroupId(groupId);
    }

    @Override
    public List<String> getAdminIdListByGroupId(String groupId) {
        return memberDao.getAdminIdListByGroupId(groupId);
    }

    @Override
    public List<GroupMember> getGroupMembers(String groupId) {
        return memberDao.getGroupMembers(groupId);
    }

    @Override
    public List<GroupMember> getGroupMembersForSendMessage(String groupId) {
        return memberDao.getGroupMembersForSendMessage(groupId);
    }

    @Override
    public GroupMember getGroupMemberForSendMessage(String groupId, String memberId) {
        return memberDao.getGroupMemberForSendMessage(groupId, memberId);
    }

    @Override
    public int getMemberTotalByGroupId(String groupId) {
        return memberDao.getMemberTotalByGroupId(groupId);
    }

    @Override
    public int getAdminTotalByGroupId(String groupId) {
        return memberDao.getAdminTotalByGroupId(groupId);
    }

    @Override
    public int updateLastCheatTime(String userId, String groupId) {
        return memberDao.updateLastCheatTime(userId, groupId, new Date());
    }

    @Override
    public int exitGroup(String userId, String groupId) {
        int res = memberDao.exitGroup(userId, groupId);
        if (res > 0){
            groupMemberCache.removeAllCache(groupId);
        }
        return res;
    }

    @Override
    public List<String> getGroupMemberIdsByGroupIdList(List<String> groupIds) {
        if (groupIds == null || groupIds.size() == 0) {
            return new ArrayList<>();
        }
        return memberDao.getGroupMemberIdsByGroupIdList(groupIds);
    }

    @Override
    public List<String> selectGroupIdListByUserUuid(String userUuid) {
        return memberDao.selectGroupIdListByUserUuid(userUuid);
    }

    @Override
    public int updateMemberStatus(String userId, String groupId, int status) {
        return memberDao.updateMemberStatus(userId, groupId, status);
    }

    @Override
    public int updateNotifyStatus(String userId, String groupId, int status) {

        int res = memberDao.updateNotifyStatus(userId, groupId, status);
        if (res > 0){
            //清除redis，第一次请求会重新从数据库加载
            groupMemberCache.removeAllCache(groupId);
        }
        return res;
    }

    @Override
    public int updateByUerIdAndGroupIdSelective(GroupMember record) {
        return memberDao.updateByUerIdAndGroupIdSelective(record);
    }

    /**
     * 批量插入，为防止内存溢出
     * 每5000条插一次
     * @param members
     * @return
     */
    @Override
    public int insertManySelective(List<GroupMember> members,String groupId) throws CustomerException {
        if (members == null || members.isEmpty()){
            throw new CustomerException("群聊用户插入信息为空！");
        }
        List<GroupMember> cache = new ArrayList<>();
        int res = 0;
        for (GroupMember member : members) {
            cache.add(member);
            if (cache.size() == 5000){
               res += memberDao.insertManySelective(cache);
                cache.clear();
            }
        }
        if (cache.size() > 0){
            res += memberDao.insertManySelective(cache);
        }
        if (res > 0){
            groupMemberCache.removeAllCache(groupId);
        }
        return res;
    }

}
