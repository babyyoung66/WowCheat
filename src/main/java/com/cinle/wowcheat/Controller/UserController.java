package com.cinle.wowcheat.Controller;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Constants.FileConst;
import com.cinle.wowcheat.Enum.FileType;
import com.cinle.wowcheat.Event.UserChangeEvent;
import com.cinle.wowcheat.GlobalException.UploadFileException;
import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Service.FriendsServices;
import com.cinle.wowcheat.Service.UserServices;
import com.cinle.wowcheat.Tools.SecurityContextUtils;
import com.cinle.wowcheat.Tools.UploadUtils;
import com.cinle.wowcheat.Vo.AjaxResponse;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Map;
import java.util.UUID;

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

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 根据wowId查找用户
     *
     * @param wowId
     * @return
     */
    @PostMapping("/queryUser")
    public AjaxResponse queryUserByWowId(String wowId) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        String wow = wowId;
        if (StringUtils.hasText(wow)) {
            MyUserDetail usr = userServices.selectByWowId(wow);
            if (usr != null) {
                return ajaxResponse.success().setData(JSON.toJSON(usr));
            }
        }
        return ajaxResponse.error().error().setMessage("该用户不存在！");
    }

    /**
     * 修改密码
     * 参数格式：
     * {
     * "name": "123",
     * "password":"qwe11011",
     * "wowId": "1515251178",
     * "uuid": "62124fee77b646417ced30b9",
     * "email": "1002709129@qq.com",
     * "oldPassword":"123456"
     * }
     *
     * @return
     */
    @PostMapping("/editPassword")
    public AjaxResponse EditPassword(@NotBlank(message = "密码不能空!")
                                     @Pattern(regexp = "[a-zA-Z0-9.!@#$%^&_;~,?]*?", message = "密码不能包含中文字符,且只能包含以下特殊字符: .!@#$%^&_;~,?")
                                     @Length(min = 6, max = 18, message = "密码长度6-18位!")
                                             String password,
                                     String oldPassword) {
        AjaxResponse response = new AjaxResponse();
        //从security上下文获取用户uuid，确保是当前用户操作
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        BCryptPasswordEncoder bcry = new BCryptPasswordEncoder();
        MyUserDetail old = userServices.selectByUUID(uuid);
        boolean success = bcry.matches(oldPassword, old.getPassword());
        if (!success) {
            response.error().setMessage("原密码不正确！");
            return response;
        }
        MyUserDetail newUsr = new MyUserDetail();
        newUsr.setUuid(uuid);
        newUsr.setPassword(password);
        int rs = userServices.updateByUUIDSelective(newUsr);
        if (rs > 0) {
            response.success().setMessage("修改成功，请重新登录！");
            ApplicationEvent event = new UserChangeEvent(newUsr);
            applicationContext.publishEvent(event); //发布事件，清除登录状态
            return response;
        }
        return response.error().setMessage("修改失败，请稍后再尝试！");
    }


    /**
     * 修改昵称
     *
     * @param name
     * @return 返回新数据
     */
    @PostMapping("editName")
    public AjaxResponse changeName(@NotBlank(message = "昵称不能为空!")
                                   @Pattern(regexp = "[^<>#^\\r\\t\\n*&\\\\/$]*?", message = "昵称不能包含特殊符号!")
                                   @Length(min = 1, max = 12, message = "昵称长度1-12位!")
                                           String name
    ) {
        AjaxResponse response = new AjaxResponse();
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        MyUserDetail usr = new MyUserDetail();
        usr.setName(name);
        usr.setUuid(uuid);
        int rs = userServices.updateByUUIDSelective(usr);
        if (rs > 0) {
            MyUserDetail detail = userServices.selectByUUID(uuid);
            return response.success().setMessage("修改成功！").setData(JSON.toJSON(detail));
        }
        return response.error().setMessage("修改失败，请重新尝试！");
    }

    /**
     * 修改头像
     *
     * @param file
     * @return
     * @throws UploadFileException
     */
    @PostMapping("/editPhoto")
    public AjaxResponse editPhoto(MultipartFile file) throws UploadFileException {
        AjaxResponse response = new AjaxResponse();
        //从security上下文获取用户uuid，确保是当前用户操作
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        MyUserDetail usr = userServices.selectByUUID(uuid);
        //将旧头像删除
        UploadUtils.removeFile(usr.getPhotourl());
        //上传新头像
        String name = file.getName();
        String ExName = name.substring(name.lastIndexOf("."));       //获取文件后缀
        String uuName = UUID.randomUUID().toString();
        String filePath = "image/" + uuName + ExName;
        UploadUtils.uploadFile(file, filePath, FileType.image);
        //存入数据库
        MyUserDetail newInfo = new MyUserDetail();
        newInfo.setPhotourl(FileConst.UPLOAD_BASE_PATH + filePath);
        newInfo.setUuid(uuid);
        userServices.updateByUUIDSelective(newInfo);

        usr.setPhotourl(FileConst.UPLOAD_BASE_PATH + filePath);
        response.success().setMessage("修改成功！").setData(JSON.toJSON(usr));//返回更新后的数据
        return response;
    }


}
