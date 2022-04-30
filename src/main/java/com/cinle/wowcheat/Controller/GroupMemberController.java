package com.cinle.wowcheat.Controller;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Service.GroupMemberService;
import com.cinle.wowcheat.Service.GroupServices;
import com.cinle.wowcheat.Service.UserServices;
import com.cinle.wowcheat.Utils.SecurityContextUtils;
import com.cinle.wowcheat.Vo.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/4/30 12:58
 */
@RestController
@RequestMapping("/groupMember")
public class GroupMemberController {
    @Autowired
    GroupServices groupServices;
    @Autowired
    GroupMemberService groupMemberService;
    @Autowired
    UserServices userServices;

    /**
     * @return
     * 获取当前用户所有群聊下的所有成员信息
     */
    @PostMapping("/getMembers")
    public AjaxResponse getMembers(){
        AjaxResponse response = new AjaxResponse();
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        List<String> groupIds = groupMemberService.selectGroupIdListByUserUuid(uuid);
        List<String> memberIds = groupMemberService.getGroupMemberIdsByGroupIdList(groupIds);
        List<MyUserDetail> members = userServices.selectUsersByUUIDs(memberIds);
        response.success().setData(JSON.toJSON(members));
        return response;
    }
}
