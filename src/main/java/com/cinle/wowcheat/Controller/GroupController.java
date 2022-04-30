package com.cinle.wowcheat.Controller;

import com.alibaba.fastjson.JSON;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/4/28 0:11
 */
@RestController
@RequestMapping("/group")
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
            long total = messageServices.getGroupUnReadTotal(g.getUuid(),time,"group");
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
}
