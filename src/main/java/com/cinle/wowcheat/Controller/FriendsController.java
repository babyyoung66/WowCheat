package com.cinle.wowcheat.Controller;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.AOP.TestUserForbidden;
import com.cinle.wowcheat.Enum.CheatStatus;
import com.cinle.wowcheat.Event.SendSocketMessageEvent;
import com.cinle.wowcheat.Model.Friends;
import com.cinle.wowcheat.Model.FriendsRequest;
import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Service.FriendRequestServices;
import com.cinle.wowcheat.Service.FriendsServices;
import com.cinle.wowcheat.Service.MessageServices;
import com.cinle.wowcheat.Service.UserServices;
import com.cinle.wowcheat.Utils.SecurityContextUtils;
import com.cinle.wowcheat.Vo.AjaxResponse;
import com.cinle.wowcheat.WebSocket.SocketMessage;
import com.cinle.wowcheat.WebSocket.SocketMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    @Autowired
    MessageServices messageServices;
    @Autowired
    FriendRequestServices friendRequestServices;
    @Autowired
    ApplicationContext applicationContext;

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
        List<MyUserDetail> users = userServices.selectByFriendsUuidList(uuids, selfUuid);
        Iterator<MyUserDetail> it = users.iterator();
        int i = 0;
        while (it.hasNext()) {
            MyUserDetail friend = it.next();
            //好友关系正常时获取未读消息数
            if (friend.getConcatInfo().getStatus() == CheatStatus.Friend_Normal.getIndex()){
                //localDateTime转date
                Timestamp time = new Timestamp(friend.getConcatInfo().getLastCheatTime().getTime());
                long total = messageServices.getPersonalUnReadTotal(friend.getUuid(), selfUuid, time, "personal");
                users.get(i).getConcatInfo().setUnReadTotal(total);
            }

            i++;
        }
        AjaxResponse ajaxResponse = new AjaxResponse().success().setData(JSON.toJSON(users));
        return ajaxResponse;
    }

    /**
     * 发送好友请求
     *
     * @param request
     * @return
     */
    @PostMapping("/sendRequest")
    public AjaxResponse SendRequest(@RequestBody @Valid FriendsRequest request) {
        AjaxResponse response = new AjaxResponse();
        String shelf = SecurityContextUtils.getCurrentUserUUID();
        request.setRequestUuid(shelf);
        //再次校验数据是否真实
        MyUserDetail usr = userServices.selectByUUID(request.getReceiverUuid());
        if (usr == null || usr.equals("")) {
            response.error().setMessage("该用户不存在！");
            return response;
        }

        //检查是否已存在好友关系
        Friends friends = friendsServices.findFriend(shelf, request.getReceiverUuid());
        if (friends != null) {
            if (1 == friends.getStatus() || 2 == friends.getStatus()) {
                return response.error().setMessage("对方已是您的好友！");
            }
            if (3 == friends.getStatus()) {
                return response.error().setMessage("对方已被您拉黑！");
            }
        }
        Friends target = friendsServices.findFriend(request.getReceiverUuid(), shelf); //对方
        if (target != null && target.getStatus() == 3) {
            return response.error().setMessage("您已被对方拉黑！");
        }
        //被自己单方删除过，但是对方未删除，则直接设置通过请求即可,无需入库
        if (target != null && target.getStatus() == 4) {
            Friends fu = new Friends();
            fu.setsUuid(request.getRequestUuid());
            fu.setfUuid(request.getReceiverUuid());
            //为自己重新添加
            friendsServices.insertSelective(fu);

            //设置对方好友为正常状态
            friendsServices.updateStatusByUuid(request.getReceiverUuid(), shelf, 1);

          /*
            //friendRequestServices.insertSelective(request);
            //发送socket事件更新前端请求列表
            SocketMessage message = new SocketMessage();
            message.success();
            message.setType(SocketMessageType.friendRequest);
            message.setMessage(request);
            SendSocketMessageEvent reqEvent = new SendSocketMessageEvent(message);
            applicationContext.publishEvent(reqEvent);*/
            //更新前端好友列表
            SocketMessage message1 = new SocketMessage();
            message1.success();
            message1.setType(SocketMessageType.updateFriend);
            message1.setMessage(request);
            SendSocketMessageEvent friendEvent = new SendSocketMessageEvent(message1);
            applicationContext.publishEvent(friendEvent);

            return response.success();
        }
        request.setRequestMessage(request.getRequestMessage().trim());
        int res = friendRequestServices.insertSelective(request);
        if (res > 0) {
            //发送socket事件更新前端请求列表
            SocketMessage message = new SocketMessage();
            message.success();
            message.setType(SocketMessageType.friendRequest);
            message.setMessage(request);
            SendSocketMessageEvent event = new SendSocketMessageEvent(message);
            applicationContext.publishEvent(event);
            return response.success();
        }
        return response.error().setMessage("插入数据失败，请校验数据真实性！");
    }

    /**
     * 获取好友请求列表
     *
     * @return
     */
    @PostMapping("/getRequestList")
    public AjaxResponse getRequestList() {
        AjaxResponse response = new AjaxResponse();
        String shelf = SecurityContextUtils.getCurrentUserUUID();
        List<FriendsRequest> list = friendRequestServices.selectListWithUserInfoByShelfUuid(shelf);
        response.success().setData(JSON.toJSON(list));
        return response;
    }


    /**
     * 同意则添加并返回好友信息
     *
     * @param request
     * @return
     */
    @PostMapping("/setRequestStatus")
    public AjaxResponse setRequestStatus(@RequestBody @Valid FriendsRequest request) {
        AjaxResponse response = new AjaxResponse();
        String shelf = SecurityContextUtils.getCurrentUserUUID();
        //只有接受者才需要更改请求状态
        request.setReceiverUuid(shelf);
        int res = friendRequestServices.updateRequestStatusByUuid(request);
        if (res > 0) {
            if (request.getRequestStatus() == 1) {
                boolean success = addFriend(request);
                if (!success) {
                    return response.error().setMessage("请尝试重新添加！");
                }
                //发送socket事件，更新好友列表(如果在线)
                SocketMessage socketMessage = new SocketMessage();
                socketMessage.success();
                socketMessage.setType(SocketMessageType.updateFriend);
                socketMessage.setMessage(request);
                SendSocketMessageEvent event = new SendSocketMessageEvent(socketMessage);
                applicationContext.publishEvent(event);
                //同时更新请求列表
                SocketMessage socketMessage1 = new SocketMessage();
                socketMessage1.success();
                socketMessage1.setType(SocketMessageType.friendRequest);
                socketMessage1.setMessage(request);
                SendSocketMessageEvent event1 = new SendSocketMessageEvent(socketMessage1);
                applicationContext.publishEvent(event1);

            }
            return response.success();
        }
        return response.error().setMessage("更新失败，请校验数据真实性");
    }

    /**
     * 接收好友请求时，接受者调用
     * @param request
     * @return
     */
    private boolean addFriend(FriendsRequest request) {
        Friends friends = new Friends();
        friends.setsUuid(request.getReceiverUuid());
        friends.setfUuid(request.getRequestUuid());
        //己方添加
        int shelfRes = friendsServices.insertSelective(friends);
        if (shelfRes <= 0) {
            return false;
        }
        int friendRes = 0;
        //查询是否被自己单方删除过
        Friends oldInfo = friendsServices.findFriend(request.getRequestUuid(), request.getReceiverUuid());
        if (oldInfo != null) {
            //重置为正常状态
            friendRes = friendsServices.updateStatusByUuid(request.getRequestUuid(), request.getReceiverUuid(), 1);
        } else {
            friends.setfUuid(request.getReceiverUuid());
            friends.setsUuid(request.getRequestUuid());
            friendRes = friendsServices.insertSelective(friends);
        }
        return 2 == shelfRes + friendRes;
    }

    /**
     * @param friends
     * @return 前端自行删除本地数据
     * 删除好友（单向）
     */
    @TestUserForbidden
    @PostMapping("/delete")
    public AjaxResponse deleteFriend(@RequestBody @Valid Friends friends) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        String shelf = SecurityContextUtils.getCurrentUserUUID();
        int rs = friendsServices.deleteByUuid(shelf, friends.getfUuid());
        if (rs > 0) {
            //更改对方状态，对方为拉黑状态时不修改
            Friends target = friendsServices.findFriendNonCache(friends.getfUuid(),shelf);
            if (target != null && target.getStatus() != CheatStatus.Friend_Block.getIndex()){
                friendsServices.updateStatusByUuid(friends.getfUuid(), shelf, CheatStatus.Friend_NotFriend.getIndex());
            }
            return ajaxResponse.success().setMessage("删除成功！");
        }
        return ajaxResponse.error().setCode(501).setMessage("操作失败！");
    }

    /**
     * 修改好友关系状态
     * 更新数据库，同时前端自行修改状态
     *
     * @param friends
     * @return
     */
    @TestUserForbidden
    @PostMapping("/changeStatus")
    public AjaxResponse changeStatus(@RequestBody @Valid Friends friends) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        String shelf = SecurityContextUtils.getCurrentUserUUID();
        String res = "";
        //检查是否满足可更改的状态
        switch (friends.getStatus()) {
            case 1:
                res = "好友状态已恢复正常！";
                break;
            case 2:
                res = "已将好友屏蔽，将不会接收好友信息！";
                break;
            case 3:
                res = "已将好友拉黑！";
                break;
            default:
                return ajaxResponse.error().setMessage("服务器拒绝了您的请求！");
        }
        int rs = friendsServices.updateStatusByUuid(shelf, friends.getfUuid(), friends.getStatus());
        if (rs < 0) {
            return ajaxResponse.error().setMessage("修改失败，请重新尝试！");
        }
        return ajaxResponse.success().setMessage(res);
    }

    /**
     * 修改好友备注
     *
     * @param friends
     * @return 返回成功状态，前端直接修改本地记录
     */
    @PostMapping("/editRemarks")
    public AjaxResponse editRemarks(@RequestBody @Valid Friends friends) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        String shelf = SecurityContextUtils.getCurrentUserUUID();
        friends.setsUuid(shelf);
//        if (!StringUtils.hasText(friends.getRemarks())) {
//            return ajaxResponse.error().setMessage("请输入备注！");
//        }
        Friends fri = friendsServices.findFriend(shelf, friends.getfUuid());
        if (fri == null) {
            return ajaxResponse.error().setMessage("对方不是您的好友！");
        }
        int rs = friendsServices.updateRemarksByUuid(friends);
        if (rs < 0) {
            return ajaxResponse.error().setMessage("修改失败，请重新尝试！");
        }
        return ajaxResponse.success().setMessage("更改成功！");
    }

    /**
     * 更新最后联系时间
     *
     * @param uuid 好友uuid
     * @return
     */
    @PostMapping("/UpdateConcatTime")
    public AjaxResponse upDateLastCheatTime(String uuid) {
        AjaxResponse response = new AjaxResponse();
        String selfUuid = SecurityContextUtils.getCurrentUserUUID();
        Friends friends = new Friends();
        friends.setsUuid(selfUuid);
        friends.setfUuid(uuid);
        int rst = friendsServices.updateLastCheatTime(friends);
        if (rst <= 0) {
            return response.error().setMessage("修改好友联系时间失败！");
        }
        return response.success().setMessage("更改成功！");
    }

}
