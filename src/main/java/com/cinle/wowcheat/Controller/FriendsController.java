package com.cinle.wowcheat.Controller;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Enum.CheatStatus;
import com.cinle.wowcheat.Model.Friends;
import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Service.FriendsServices;
import com.cinle.wowcheat.Service.UserServices;
import com.cinle.wowcheat.Tools.SecurityContextUtils;
import com.cinle.wowcheat.Vo.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/2/28 15:26
 */
@RestController
@RequestMapping("/friend")
@Validated
public class FriendsController {
    @Autowired
    FriendsServices friendsServices;
    @Autowired
    UserServices userServices;


    /**
     * 根据当前请求用户获取
     * 无需传参
     *
     * @return
     */
    @PostMapping("/getList")
    public AjaxResponse getFriendsList() {
        //从security上下文获取请求者信息
        String selfUuid = SecurityContextUtils.getCurrentUserUUID();
        List<String> uuids = friendsServices.selectFriendUuidList(selfUuid);
        List users = userServices.selectByFriendsUuidList(uuids, selfUuid);
        //转一遍string再再转会数组是为了去掉null的字段 JSON.parseArray(JSON.toJSONString(users))
        AjaxResponse ajaxResponse = new AjaxResponse().success().setData(JSON.toJSON(users));
        return ajaxResponse;
    }

    /**
     * @param friends
     * @return 返回新列表供更新
     * 添加好友（双向）
     * 暂未添加请求确认步骤
     */
    @PostMapping("/add")
    public AjaxResponse addFriend(@RequestBody @Valid Friends friends) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        String selfUuid = SecurityContextUtils.getCurrentUserUUID();
        String fUUid = friends.getfUuid();
        MyUserDetail usr = userServices.selectByUUID(fUUid);
        Friends isFriend = friendsServices.findFriend(selfUuid, fUUid); //自己

        if (usr == null || usr.equals("")) {
            ajaxResponse.error().setMessage("该用户不存在！");
            return ajaxResponse;
        }
        if (isFriend != null ) {
            int status = isFriend.getStatus();
            if (status == 3) {
                ajaxResponse.error().setMessage("您已将对方拉黑！");
            } else {
                ajaxResponse.error().setMessage("该用户已是您的好友！");
            }
            return ajaxResponse;
        }
        Friends target = friendsServices.findFriend(fUUid, selfUuid); //对方
        if (target != null && target.getStatus() == 3) {
            return ajaxResponse.error().setMessage("您已被对方拉黑！");
        }
        friends.setsUuid(selfUuid);
        //己方添加
        friendsServices.insertSelective(friends);
        if (target == null) {
            //对方已添加，不重复插入,互换uuid位置
            friends.setsUuid(fUUid);
            friends.setfUuid(selfUuid);
            friendsServices.insertSelective(friends);
        }
        List<String> uuids = friendsServices.selectFriendUuidList(selfUuid);
        List users = userServices.selectByFriendsUuidList(uuids, selfUuid);

        return ajaxResponse.success().setMessage("添加成功！").setData(JSON.toJSON(users));
    }

    /**
     * @param friends
     * @return 返回新列表
     * 删除好友（单向）
     */
    @PostMapping("/delete")
    public AjaxResponse deleteFriend(@RequestBody @Valid Friends friends) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        String shelf = SecurityContextUtils.getCurrentUserUUID();
        friendsServices.deleteByUuid(shelf, friends.getfUuid());
        friendsServices.updateStatusByUuid(friends.getfUuid(), shelf, CheatStatus.Friend_NotFriend.getIndex());
        List<String> uuids = friendsServices.selectFriendUuidList(shelf);
        List users = userServices.selectByFriendsUuidList(uuids, shelf);
        return ajaxResponse.success().setMessage("删除成功！").setData(JSON.toJSON(users));
    }

    /**
     * 修改好友关系状态
     *
     * @param friends
     * @return
     */
    @PostMapping("/changeStatus")
    public AjaxResponse changeStatus(@RequestBody @Valid Friends friends) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        String shelf = SecurityContextUtils.getCurrentUserUUID();
        int rs = friendsServices.updateStatusByUuid(shelf, friends.getfUuid(), friends.getStatus());
        if (rs < 0) {
            return ajaxResponse.error().setMessage("修改失败，请重新尝试！");
        }
        List<String> uuids = friendsServices.selectFriendUuidList(shelf);
        List users = userServices.selectByFriendsUuidList(uuids, shelf);
        return ajaxResponse.success().setMessage("更改成功！").setData(JSON.toJSON(users));
    }

    /**
     * 修改好友备注
     *
     * @param friends 返回新列表
     * @return
     */
    @PostMapping("/editRemarks")
    public AjaxResponse editRemarks(@RequestBody @Valid Friends friends) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        String shelf = SecurityContextUtils.getCurrentUserUUID();
        friends.setsUuid(shelf);
        if (!StringUtils.hasText(friends.getRemarks())) {
            return ajaxResponse.error().setMessage("请输入备注！");
        }
        Friends fri = friendsServices.findFriend(shelf, friends.getfUuid());
        if (fri == null) {
            return ajaxResponse.error().setMessage("对方不是您的好友！");
        }
        int rs = friendsServices.updateRemarksByUuid(friends);
        if (rs < 0) {
            return ajaxResponse.error().setMessage("修改失败，请重新尝试！");
        }
        List<String> uuids = friendsServices.selectFriendUuidList(shelf);
        List users = userServices.selectByFriendsUuidList(uuids, shelf);
        return ajaxResponse.success().setMessage("更改成功！").setData(JSON.toJSON(users));
    }

}
