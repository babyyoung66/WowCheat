package com.cinle.wowcheat.Controller;

import com.cinle.wowcheat.GlobalException.ControllerExceptionDeal;
import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Service.UserServices;
import com.cinle.wowcheat.Vo.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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

    /**
     * @param user 用户JSON
     * @return 注册信息
     * @see ControllerExceptionDeal 注册失败信息已全局捕获
     */
    @RequestMapping("/register")
    public AjaxResponse userRegister(@RequestBody @Valid MyUserDetail user) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        userServices.insertSelective(user);
        ajaxResponse.success().setMessage("注册成功!");
        return ajaxResponse;
    }

    @RequestMapping("/insert")
    @PreAuthorize("hasAnyAuthority('admin')")
    public AjaxResponse insert(@RequestBody @Valid MyUserDetail user) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        userServices.insertSelective(user);
        ajaxResponse.success().setMessage("注册成功!");
        return ajaxResponse;
    }
}
