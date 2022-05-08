package com.cinle.wowcheat.Service;

import com.cinle.wowcheat.Exception.CustomerException;
import com.cinle.wowcheat.Model.GroupMember;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/4/17 19:34
 */
public interface GroupMemberService {
    int deleteByPrimaryKey(Integer autoId);


    int insertSelective(GroupMember record);

    GroupMember selectByPrimaryKey(Integer autoId);

    GroupMember selectByGroupIdAndMemberId(String groupId,String memberId);

    int updateByPrimaryKeySelective(GroupMember record);


    List<String> getMemberIdListByGroupId(String groupId);

    List<String> getAdminIdListByGroupId(String groupId);

    List<GroupMember> getGroupMembers(String groupId);

    List<GroupMember> getGroupMembersForSendMessage(String groupId);

    GroupMember getGroupMemberForSendMessage(String groupId, String memberId);

    int getMemberTotalByGroupId(String groupId);

    int getAdminTotalByGroupId(String groupId);

    int updateLastCheatTime(String userId,String groupId);

    int exitGroup(String userId,String groupId);

    /**
     * 获取多个群组所有的member
     */
    List<String> getGroupMemberIdsByGroupIdList( List<String> groupIds);

    /**
     * 获取用户所有群组的uuid
     */
    List<String> selectGroupIdListByUserUuid(String userUuid);

    int updateMemberStatus( String userId, String groupId, int status);

    int updateNotifyStatus( String userId, String groupId,  int status);

    int updateByUerIdAndGroupIdSelective(GroupMember record);

    int insertManySelective(List<GroupMember> members,String groupId) throws CustomerException;
}
