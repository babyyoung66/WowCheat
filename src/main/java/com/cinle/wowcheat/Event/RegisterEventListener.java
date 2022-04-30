package com.cinle.wowcheat.Event;

import com.cinle.wowcheat.Constants.MyConst;
import com.cinle.wowcheat.Model.Friends;
import com.cinle.wowcheat.Model.GroupMember;
import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Service.FriendsServices;
import com.cinle.wowcheat.Service.GroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Author JunLe
 * @Time 2022/4/30 14:05
 */
@Component
public class RegisterEventListener {
    @Autowired
    FriendsServices friendsServices;
    @Autowired
    GroupMemberService groupMemberService;


    @Async("AsyncExecutor")
    @EventListener
    public void afterRegister(RegisterEvent event){
        MyUserDetail user = (MyUserDetail) event.getSource();
        //默认添加admin为好友
        Friends friends = new Friends();
        friends.setsUuid(user.getUuid());
        friends.setfUuid(MyConst.DEFAULT_ADMIN_ID);
        friends.setRemarks("管理员");
        //双方添加
        Friends friends1 = new Friends();
        friends1.setfUuid(user.getUuid());
        friends1.setsUuid(MyConst.DEFAULT_ADMIN_ID);

        friendsServices.insertSelective(friends1);
        friendsServices.insertSelective(friends);

        //默认进入测试群聊
        GroupMember groupMember = new GroupMember();
        groupMember.setGroupUuid(MyConst.DEFAULT_GROUP_ID);
        groupMember.setUserUuid(user.getUuid());
        groupMember.setInviterUuid(MyConst.DEFAULT_ADMIN_ID);
        groupMemberService.insertSelective(groupMember);

    }
}
