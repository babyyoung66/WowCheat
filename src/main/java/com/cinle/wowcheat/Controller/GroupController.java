package com.cinle.wowcheat.Controller;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.AOP.TestUserForbidden;
import com.cinle.wowcheat.Constants.MessageConst;
import com.cinle.wowcheat.Enum.CheatStatus;
import com.cinle.wowcheat.Event.SendSocketMessageEvent;
import com.cinle.wowcheat.Exception.CustomerException;
import com.cinle.wowcheat.Model.Friends;
import com.cinle.wowcheat.Model.Group;
import com.cinle.wowcheat.Model.GroupMember;
import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Service.*;
import com.cinle.wowcheat.Utils.SecurityContextUtils;
import com.cinle.wowcheat.Vo.AjaxResponse;
import com.cinle.wowcheat.Vo.InviteMemberVo;
import com.cinle.wowcheat.WebSocket.SocketMessage;
import com.cinle.wowcheat.WebSocket.SocketMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.*;

/**
 * @Author JunLe
 * @Time 2022/4/28 0:11
 */
@RestController
@RequestMapping("/group")
@Validated
public class GroupController {
    @Autowired
    GroupServices groupServices;
    @Autowired
    GroupMemberService groupMemberService;
    @Autowired
    MessageServices messageServices;
    @Autowired
    UserServices userServices;
    @Autowired
    FriendsServices friendsServices;
    @Autowired
    ApplicationContext applicationContext;

    @PostMapping("/getGroupList")
    public AjaxResponse getGroups() {
        AjaxResponse response = new AjaxResponse();
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        List<Group> groups = groupServices.selectGroupsByUserUuid(uuid);
        for (int i = 0; i < groups.size(); i++) {
            Group g = groups.get(i);
            Date cheatTime = g.getConcatInfo().getLastCheatTime();
            Timestamp time = new Timestamp(MessageConst.getQueryStartTime().getTime());
            if (cheatTime != null) {
                time = new Timestamp(cheatTime.getTime());
            }
            //查询没有屏蔽的群聊未读消息数
            if ( g.getConcatInfo().getNotifyStatus() == 0){
                long total = messageServices.getGroupUnReadTotal(uuid, g.getUuid(), time, "group");
                groups.get(i).getConcatInfo().setUnReadTotal(total);
            }
        }
        response.success().setData(JSON.toJSON(groups));
        return response;
    }

    /**
     * x-www-form请求
     * 查询与请求者自己相关的群聊
     * @param groupId
     * @return
     */
    @PostMapping("/getGroupInfo")
    public AjaxResponse getGroupInfo(@NotBlank(message = "groupId不能为空！") String groupId){
        AjaxResponse response = new AjaxResponse();
        String shelfId = SecurityContextUtils.getCurrentUserUUID();
        Group group = groupServices.selectByUuid(groupId);
        if (group == null){
            return response.error().setMessage("该群聊不存在！");
        }
        if (!group.getMemberIds().contains(shelfId)){
            return response.error().setMessage("您未加入该群聊！");
        }
        GroupMember concatInfo = groupMemberService.selectByGroupIdAndMemberId(groupId,shelfId);
        group.setConcatInfo(concatInfo);
        response.success().setData(JSON.toJSON(group));
        return response;
    }

    @PostMapping("/UpdateConcatTime")
    public AjaxResponse upDateLastCheatTime(String uuid) {
        AjaxResponse response = new AjaxResponse();
        String selfUuid = SecurityContextUtils.getCurrentUserUUID();
        int rst = groupMemberService.updateLastCheatTime(selfUuid, uuid);
        if (rst <= 0) {
            return response.error().setMessage("修改群聊联系时间失败！");
        }
        return response.success().setMessage("更改成功！");
    }

    /**
     * 退出群聊,x-www-form请求
     * 发送socket信息，让群员更新群信息
     * @param groupId
     * @return
     */
    @TestUserForbidden
    @PostMapping("/exitGroup")
    public AjaxResponse exitGroup(String groupId) {
        AjaxResponse response = new AjaxResponse();
        String userId = SecurityContextUtils.getCurrentUserUUID();
        int res = groupMemberService.exitGroup(userId, groupId);
        if (res > 0) {
            //发送socket通知更新群信息
            Group newInfo = groupServices.selectByUuid(groupId);
            SocketMessage message = new SocketMessage();
            message.setType(SocketMessageType.updateGroup);
            message.success().setMessage(newInfo);
            SendSocketMessageEvent event = new SendSocketMessageEvent(message);
            applicationContext.publishEvent(event);
            return response.success().setMessage("已退出该群聊！");
        }
        return response.error().setCode(501).setMessage("未能成功退出该群聊，请重新尝试！");
    }

    @PostMapping("/changeNotifyStatus")
    public AjaxResponse changeNotifyStatus(@Valid @RequestBody GroupMember member) {
        AjaxResponse response = new AjaxResponse();
        String userId = SecurityContextUtils.getCurrentUserUUID();
        int res = groupMemberService.updateNotifyStatus(userId, member.getGroupUuid(), member.getNotifyStatus());
        if (res > 0) {
            return response.success().setMessage("修改成功");
        }
        return response.error().setCode(501).setMessage("修改失败，请重新尝试！");
    }

    @PostMapping("/editRemarks")
    public AjaxResponse editRemarks(@Valid @RequestBody GroupMember member) {
        AjaxResponse response = new AjaxResponse();
        String userId = SecurityContextUtils.getCurrentUserUUID();
        member.setUserUuid(userId);
        int res = groupMemberService.updateByUerIdAndGroupIdSelective(member);
        if (res > 0) {
            return response.success().setMessage("修改成功");
        }
        return response.error().setCode(501).setMessage("修改失败，请重新尝试！");
    }

    /**
     * json请求
     * 1.验证请求者是否在当前群聊
     * 2.查出所有用户资料
     * 3.查出所有与请求者相关的好友状态
     * 5.生成两个HashMap（用户资料，好友状态）
     * 6.将状态不正常的返回给请求者
     * 7.成功插入的信息socket推送给其他群成员 （暂时不做）
     * 8.通知所有群成员更新群信息
     *  传入map，存以下两个参数
     *  groupId   群聊uuid
     *  memberIds 用户uuid
     * @return 返回邀请成功的信息及邀请失败的信息
     */
    @PostMapping("/inviteMembers")
    public AjaxResponse inviteMembers(@RequestBody Map info) throws CustomerException {
        String groupId = (String) info.get("groupId");
        List<String> memberIds = (List<String>) info.get("memberIds");
        AjaxResponse response = new AjaxResponse();
        if (memberIds == null || memberIds.size() == 0) {
            return response.error().setMessage("请选择好友！");
        }
        if (!StringUtils.hasText(groupId)){
            return response.error().setMessage("请选择群聊！");
        }
        String shelfId = SecurityContextUtils.getCurrentUserUUID();
        MyUserDetail shelfInfo = userServices.selectByUUID(shelfId);
        GroupMember checkInfo = groupMemberService.selectByGroupIdAndMemberId(groupId, shelfId);
        if (checkInfo == null) {
            return response.error().setMessage("您未加入该群聊！");
        }
        //群里所有用户ID
        List<String> groupCurrentMembers = groupMemberService.getMemberIdListByGroupId(groupId);
        //列表所有用户信息
        List<MyUserDetail> users = userServices.selectUsersByUUIDs(memberIds);
        HashMap<String, MyUserDetail> userMap = new HashMap();
        for (MyUserDetail user : users) {
            userMap.put(user.getUuid(), user);
        }
        //请求者相关好友的与请求者的状态(好友视角与自己的关系)
        List<Friends> friends = friendsServices.selectShelfInfoByFriendIdList(shelfId, memberIds);
        HashMap<String, Friends> friendMap = new HashMap();
        for (Friends friend : friends) {
            friendMap.put(friend.getsUuid(), friend);
        }
        //保存验证通过的正常的member，socket推送给所有成员
        List<GroupMember> insertMembers = new ArrayList<>();
        //保存验证错误信息,http直接返回给请求者
        List<InviteMemberVo> errorResult = new ArrayList<>();
        //保存成功信息
        List<InviteMemberVo> successResult = new ArrayList<>();
        Date joinTime = new Date();
        for (String uuid : memberIds) {
            MyUserDetail usr = userMap.get(uuid);
            Friends fri = friendMap.get(uuid);
            InviteMemberVo result = new InviteMemberVo();
            if (usr == null) {
                result.setUuid(uuid).setSuccess(false).setMessage("用户不存在");
                errorResult.add(result);
                continue;
            }
            if (groupCurrentMembers.contains(uuid)) {
                result.setUuid(uuid).setName(usr.getName()).setSuccess(false).setMessage("已在群聊中");
                errorResult.add(result);
                continue;
            }
            if (fri == null) {
                result.setUuid(uuid).setName(usr.getName()).setSuccess(false).setMessage("还不是您的好友");
                continue;
            }
            if (fri.getStatus() == CheatStatus.Friend_Block.getIndex()) {
                result.setUuid(uuid).setName(usr.getName()).setSuccess(false).setMessage("已将您拉黑");
                errorResult.add(result);
                continue;
            }
            if (fri.getStatus() == CheatStatus.Friend_Normal.getIndex()) {
                GroupMember member = new GroupMember();
                member.setJoinTime(joinTime);
                member.setInviterUuid(shelfId);
                member.setGroupUuid(groupId);
                member.setUserUuid(uuid);
                insertMembers.add(member);
                result.setInvitorName(shelfInfo.getName()).setUuid(uuid).setName(usr.getName()).setSuccess(true);
                successResult.add(result);
            }

        }
        if (insertMembers.size() > 0) {
            int res = groupMemberService.insertManySelective(insertMembers,groupId);
            if (res > 0) {
                //socket推送入群消息
                List<InviteMemberVo> subResult = new ArrayList<>();
                subResult.addAll(successResult);
                subResult.addAll(errorResult);
                //发送socket通知更新群信息
                Group newInfo = groupServices.selectByUuid(groupId);
                SocketMessage message = new SocketMessage();
                message.setType(SocketMessageType.updateGroup);
                message.success().setMessage(newInfo);
                SendSocketMessageEvent event = new SendSocketMessageEvent(message);
                applicationContext.publishEvent(event);

                return response.success().setData(JSON.toJSON(subResult));
            }
        }

        return response.success().setData(JSON.toJSON(errorResult));
    }

}
