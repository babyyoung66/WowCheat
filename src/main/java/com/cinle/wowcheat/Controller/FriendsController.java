package com.cinle.wowcheat.Controller;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Model.Friends;
import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Service.FriendsServices;
import com.cinle.wowcheat.Service.UserServices;
import com.cinle.wowcheat.Vo.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
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


    @PostMapping("/getList")
    public AjaxResponse getFriendsList(@RequestBody MyUserDetail userDetail) {
        List<String> uuids = friendsServices.selectFriendUuidList(userDetail.getUuid());
        List users = userServices.selectByFriendsUuidList(uuids, userDetail.getUuid());

        AjaxResponse ajaxResponse = new AjaxResponse().success().setData(JSON.toJSON(users));
        return ajaxResponse;
    }

    /**
     * @param friends
     * @return 添加好友（双向）
     * 暂未添加请求确认步骤
     */
    @PostMapping("/add")
    public AjaxResponse addFriend(@RequestBody @Valid Friends friends) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        MyUserDetail usr = userServices.selectByUUID(friends.getfUuid());
        Friends shelf = friendsServices.findFriend(friends.getsUuid(), friends.getfUuid()); //自己
        Friends target = friendsServices.findFriend(friends.getsUuid(), friends.getfUuid()); //对方
        if (usr == null || usr.equals("")) {
            ajaxResponse.error().setMessage("该用户不存在！");
            return ajaxResponse;
        }
        if (shelf != null || !shelf.equals("")) {
            int status = shelf.getStatus();
            if (status == 3) {
                ajaxResponse.error().setMessage("您已将对方拉黑！");
            } else {
                ajaxResponse.error().setMessage("该用户已是您的好友！");
            }
            return ajaxResponse;
        }
        if (target != null && target.getStatus() == 3) {
            return ajaxResponse.error().setMessage("您已被拉黑！");
        }
        //己方添加
        friendsServices.insertByUuid(friends.getsUuid(), friends.getfUuid());
        if (target == null) {
            //对方已添加，不重复插入
            friendsServices.insertByUuid(friends.getfUuid(), friends.getsUuid());
        }
        return ajaxResponse.success().setMessage("添加成功！");
    }

    /**
     * @param friends
     * @return 返回新列表
     * 删除好友（单向）
     */
    @PostMapping("/delete")
    public AjaxResponse deleteFriend(@RequestBody @Valid Friends friends) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        friendsServices.deleteByUuid(friends.getsUuid(), friends.getfUuid());
        List<String> uuids = friendsServices.selectFriendUuidList(friends.getsUuid());
        List users = userServices.selectByFriendsUuidList(uuids, friends.getsUuid());
        return ajaxResponse.success().setMessage("删除成功！").setData(JSON.toJSON(users));
    }

    @PostMapping("/changeStatus")
    public AjaxResponse changeStatus(@RequestBody @Valid Friends friends){
        AjaxResponse ajaxResponse = new AjaxResponse();
        friendsServices.updateStatusByUuid(friends);
        List<String> uuids = friendsServices.selectFriendUuidList(friends.getsUuid());
        List users = userServices.selectByFriendsUuidList(uuids, friends.getsUuid());
        return ajaxResponse.success().setMessage("更改成功！").setData(JSON.toJSON(users));
    }

}
