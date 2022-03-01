package com.cinle.wowcheat.Controller;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Service.FriendsServices;
import com.cinle.wowcheat.Service.UserServices;
import com.cinle.wowcheat.Vo.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author JunLe
 * @Time 2022/2/20 21:33
 */

//开启校验功能
@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServices userServices;

    @Autowired
    FriendsServices friendsServices;

    @PostMapping("/queryUser")
    public AjaxResponse queryUserByWowId(@RequestBody Map<String, String> map) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        String wow = map.get("wowId");
        if (StringUtils.hasText(wow)) {
            MyUserDetail usr = userServices.selectByWowId(wow);
            if (usr != null) {
                return ajaxResponse.success().setData(JSON.toJSON(usr));
            }
        }
        return ajaxResponse.error().error().setMessage("该用户不存在！");
    }

    //public AjaxResponse EditPhoto(@RequestBody )

}
