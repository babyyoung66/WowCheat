package com.cinle.wowcheat.Controller;

import cn.hutool.core.util.StrUtil;
import com.cinle.wowcheat.GlobalException.GlobalExceptionDeal;
import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Service.SendMailServices;
import com.cinle.wowcheat.Service.UserServices;
import com.cinle.wowcheat.Service.VerifyService;
import com.cinle.wowcheat.Vo.AjaxResponse;
import com.cinle.wowcheat.Vo.RegisterVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


/**
 * @Author JunLe
 * @Time 2022/2/21 11:34
 * 提供注册处理
 */

@Api(tags = "用户注册接口")
@Validated  //开启校验功能
@RestController
@RequestMapping("/register")
public class RegisterController {
    @Autowired
    UserServices userServices;

    @Autowired
    SendMailServices mailServices;

    @Autowired
    VerifyService verifyService;



    /**
     * @param user 用户JSON
     * @return 注册信息
     * @see GlobalExceptionDeal 注册失败信息已全局捕获
     */
    @ApiOperation(value = "提交注册信息",notes = "")
    @PostMapping("/")
    public AjaxResponse userRegister(@RequestBody @Valid MyUserDetail user) {

        AjaxResponse ajaxResponse = new AjaxResponse();
        List<MyUserDetail> list =  userServices.selectByWowIdOrEmail(user);
        System.out.println();
        if (!list.isEmpty() ||list.size()>0){
            return ajaxResponse.error().setMessage("id或邮箱已被注册!");
        }
        /* 邮箱验证，验证通过则放行*/
        if (verifyService.isEmailCheckSuccess(user.getEmail())){
            userServices.insertSelective(user);
            return ajaxResponse.success().setMessage("注册成功!");
        }
        return ajaxResponse.error().setMessage("注册失败,请重新尝试!");
    }

    /**
     * 发送邮件验证码
     * @param user
     */
    @ApiOperation(value = "获取邮件验证码",notes = "")
    @PostMapping("/postEmailCode")
    public AjaxResponse postEmailCode(@RequestBody @Valid RegisterVo user) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        String email = user.getEmail();
        if (email == null || StrUtil.hasBlank(email)) {
            return ajaxResponse.error().setMessage("请输入邮箱!");
        }
        long time =  mailServices.SendVerifyMail(user.getEmail());
        if (time == 0){
            return ajaxResponse.success().setMessage("邮件已发送，请注意查收!");
        }
        return ajaxResponse.error().setMessage("验证码5分钟内有效，如未收到请"+ time +"秒后再尝试!");
    }

    /**检查邮箱验证码
     * @param user
     * @return
     */
    @ApiOperation(value = "校验邮件验证码",notes = "")
    @PostMapping("/checkEmailCode")
    public AjaxResponse checkEmailCode(@RequestBody @Valid RegisterVo user){
        AjaxResponse ajaxResponse = new AjaxResponse();
        /* 从redis获取并验证 */
        String success = verifyService.checkEmailCode(user.getEmail(),user.getCode());
        if (success != null && !success.isEmpty()){
            return ajaxResponse.success().setMessage(success);
        }
        return ajaxResponse.error().setMessage("验证码错误!");
    }

    @ApiOperation(value = "检查Email是否可用",notes = "")
    @PostMapping("/isEmailHasRegister")
    public AjaxResponse isEmailHasRegister(@RequestBody @Valid RegisterVo user){
        AjaxResponse ajaxResponse = new AjaxResponse();
        if (user.getEmail() == null || "".equals(user.getEmail())){
            return ajaxResponse.error().setMessage("请输入邮箱!");
        }
        MyUserDetail info = new MyUserDetail();
        info.setEmail(user.getEmail());
        List list = userServices.selectByWowIdOrEmail(info);

        if (list == null || !list.isEmpty()){
            return ajaxResponse.error().setMessage("邮箱已被占用!");
        }
        return ajaxResponse.success().setMessage("邮箱可以使用!");
    }

    @ApiOperation(value = "检查Id是否可用",notes = "")
    @PostMapping("/isIdHasRegister")
    public AjaxResponse isIdHasRegister(@org.jetbrains.annotations.NotNull @RequestBody @Valid RegisterVo user){
        AjaxResponse ajaxResponse = new AjaxResponse();
        if (user.getWowId() == null || "".equals(user.getWowId())){
            return ajaxResponse.error().setMessage("请输入Id!");
        }
        MyUserDetail info = new MyUserDetail();
        info.setWowId(user.getWowId());

        List list = userServices.selectByWowIdOrEmail(info);

        if (list == null || !list.isEmpty()){
            return ajaxResponse.error().setMessage("Id已被占用!");
        }
        return ajaxResponse.success().setMessage("Id可以使用!");
    }

}
