package com.cinle.wowcheat.Service.Impl;

import com.cinle.wowcheat.Dao.GroupMemberDao;
import com.cinle.wowcheat.Model.GroupMember;
import com.cinle.wowcheat.Redis.GroupMemberCache;
import com.cinle.wowcheat.Service.GroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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
    public int insert(GroupMember record) {
        record.setJoinTime(new Date());
        //更新redis
        groupMemberCache.updateGroupMemberIds(record.getGroupUuid(), Arrays.asList(record.getUserUuid()));

        return memberDao.insert(record);
    }

    @Override
    public int insertSelective(GroupMember record) {
        record.setJoinTime(new Date());
        return memberDao.insertSelective(record);
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
    public int updateByPrimaryKey(GroupMember record) {
        return updateByPrimaryKey(record);
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
        //更新redis
        groupMemberCache.deleteGroupMemberId(groupId, Arrays.asList(userId));
        return memberDao.exitGroup(userId, groupId);
    }

    @Override
    public List<String> getGroupMemberIdsByGroupIdList(List<String> groupIds) {
        if (groupIds == null || groupIds.size() == 0){
            return new ArrayList<>();
        }
        return memberDao.getGroupMemberIdsByGroupIdList(groupIds);
    }

    @Override
    public List<String> selectGroupIdListByUserUuid(String userUuid) {
        return memberDao.selectGroupIdListByUserUuid(userUuid);
    }
}
