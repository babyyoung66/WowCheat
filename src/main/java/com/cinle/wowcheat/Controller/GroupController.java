package com.cinle.wowcheat.Controller;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.AOP.TestUserForbidden;
import com.cinle.wowcheat.Constants.MessageConst;
import com.cinle.wowcheat.Model.Friends;
import com.cinle.wowcheat.Model.Group;
import com.cinle.wowcheat.Model.GroupMember;
import com.cinle.wowcheat.Service.GroupMemberService;
import com.cinle.wowcheat.Service.GroupServices;
import com.cinle.wowcheat.Service.MessageServices;
import com.cinle.wowcheat.Utils.SecurityContextUtils;
import com.cinle.wowcheat.Vo.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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

    @PostMapping("/getGroupList")
    public AjaxResponse getGroups(){
        AjaxResponse response = new AjaxResponse();
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        List<Group> groups = groupServices.selectGroupsByUserUuid(uuid);
        for (int i = 0; i < groups.size(); i++) {
            Group g = groups.get(i);
            Date cheatTime = g.getConcatInfo().getLastCheatTime();
            Timestamp time = new Timestamp(MessageConst.getQueryStartTime().getTime());
            if (cheatTime != null){
                time = new Timestamp(cheatTime.getTime());
            }
            long total = messageServices.getGroupUnReadTotal(uuid,g.getUuid(),time,"group");
            groups.get(i).getConcatInfo().setUnReadTotal(total);
        }
        response.success().setData(JSON.toJSON(groups));
        return response;
    }

    @PostMapping("/UpdateConcatTime")
    public AjaxResponse upDateLastCheatTime(String uuid) {
        AjaxResponse response = new AjaxResponse();
        String selfUuid = SecurityContextUtils.getCurrentUserUUID();
        int rst = groupMemberService.updateLastCheatTime(selfUuid,uuid);
        if (rst <= 0) {
            return response.error().setMessage("修改群聊联系时间失败！");
        }
        return response.success().setMessage("更改成功！");
    }

    /**
     * 退出群聊,x-www-form请求
     * @param groupId
     * @return
     */
    @TestUserForbidden
    @PostMapping("/exitGroup")
    public AjaxResponse exitGroup(String groupId){
        AjaxResponse response = new AjaxResponse();
        String userId = SecurityContextUtils.getCurrentUserUUID();
        int res = groupMemberService.exitGroup(userId,groupId);
        if (res > 0){
           return response.success().setMessage("已退出该群聊！");
        }
        return response.error().setCode(501).setMessage("未能成功退出该群聊，请重新尝试！");
    }

    @PostMapping("/changeNotifyStatus")
    public AjaxResponse changeNotifyStatus(@Valid @RequestBody GroupMember member){
        AjaxResponse response = new AjaxResponse();
        String userId = SecurityContextUtils.getCurrentUserUUID();
        int res = groupMemberService.updateNotifyStatus(userId,member.getGroupUuid(),member.getNotifyStatus());
        if (res > 0){
            return response.success().setMessage("修改成功");
        }
        return response.error().setCode(501).setMessage("修改失败，请重新尝试！");
    }


}
