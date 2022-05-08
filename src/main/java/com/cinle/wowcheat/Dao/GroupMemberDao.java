package com.cinle.wowcheat.Dao;

import com.cinle.wowcheat.Model.GroupMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Mapper
public interface GroupMemberDao {
    int deleteByPrimaryKey(Integer autoId);


    int insertSelective(GroupMember record);

    GroupMember selectByPrimaryKey(Integer autoId);

    GroupMember selectByGroupIdAndMemberId(@Param("groupId") String groupId, @Param("memberId") String memberId);

    int updateByPrimaryKeySelective(GroupMember record);


    List<String> getMemberIdListByGroupId(@Param("groupId") String groupId);

    List<String> getAdminIdListByGroupId(@Param("groupId") String groupId);

    List<GroupMember> getGroupMembers(@Param("groupId") String groupId);

    /**
     * @param groupId
     * @return 只查询 user_uuid,member_role, member_status ,notify_status
     */
    List<GroupMember> getGroupMembersForSendMessage(@Param("groupId") String groupId);

    GroupMember getGroupMemberForSendMessage(@Param("groupId") String groupId, @Param("memberId") String memberId);

    int getMemberTotalByGroupId(@Param("groupId") String groupId);

    int getAdminTotalByGroupId(@Param("groupId") String groupId);

    int updateLastCheatTime(@Param("userId") String userId, @Param("groupId") String groupId, @Param("time") Date time);

    int exitGroup(@Param("userId") String userId, @Param("groupId") String groupId);

    /**
     * 获取多个群组所有的member
     */
    List<String> getGroupMemberIdsByGroupIdList(@Param("groupIds") List<String> groupIds);

    /**
     * 获取用户所有群组的uuid
     */
    List<String> selectGroupIdListByUserUuid(@Param("userUuid") String userUuid);

    int updateMemberStatus(@Param("userId") String userId, @Param("groupId") String groupId, @Param("status") int status);

    int updateNotifyStatus(@Param("userId") String userId, @Param("groupId") String groupId, @Param("status") int status);

    int updateByUerIdAndGroupIdSelective(GroupMember record);

    /**
     * 批量插入
     * 只包含初始的四个字段：group_uuid, user_uuid, join_time, inviter_uuid
     */
    int insertManySelective(@Param("members") List<GroupMember> members);
}