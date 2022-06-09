package com.cinle.wowcheat.Controller;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.AOP.TestUserForbidden;
import com.cinle.wowcheat.Constants.FileConst;
import com.cinle.wowcheat.Constants.MyConst;
import com.cinle.wowcheat.Dao.UserFileDetailDao;
import com.cinle.wowcheat.Enum.FileType;
import com.cinle.wowcheat.Enum.RoleEnum;
import com.cinle.wowcheat.Event.ImagePressEvent;
import com.cinle.wowcheat.Event.UserChangeEvent;
import com.cinle.wowcheat.Exception.UploadFileException;
import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Model.UserFileDetail;
import com.cinle.wowcheat.Service.FriendsServices;
import com.cinle.wowcheat.Service.RoleServices;
import com.cinle.wowcheat.Service.UserServices;
import com.cinle.wowcheat.Utils.SecurityContextUtils;
import com.cinle.wowcheat.Utils.UploadUtils;
import com.cinle.wowcheat.Vo.AjaxResponse;
import com.cinle.wowcheat.Vo.UserEditVo;
import io.swagger.annotations.Api;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author JunLe
 * @Time 2022/2/20 21:33
 */
@Api
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

    @Autowired
    RoleServices roleServices;


    @PostMapping("/getTestUser")
    public AjaxResponse getTestUser() {
        AjaxResponse ajaxResponse = new AjaxResponse();
        List<String> Ids = roleServices.selectUserIdsByRoleType(RoleEnum.TEST);
        List<MyUserDetail> users = userServices.selectUsersByUUIDs(Ids);
        if (users != null) {
            return ajaxResponse.success().setData(JSON.toJSON(users));
        }
        return ajaxResponse.error().setMessage("未查询到可用的测试账号，请自行注册！");
    }

    /**
     * 根据wowId查找用户
     *
     * @param wowId x-www-form形式请求
     * @return 返回用户信息，前端自行判断非空
     */

    @PostMapping("/queryUser")
    public AjaxResponse queryUserByWowId(
                                         @NotBlank(message = "Wow号不能为空!")
                                         @Pattern(regexp = "[a-zA-Z0-9]*", message = "Wow号不能包含中文字符及特殊符号!")
                                         @Length(min = 5, max = 18, message = "Wow号长度5-18位!")
                                                 String wowId) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        MyUserDetail usr = userServices.selectByWowId(wowId);
        if (usr != null) {
            ajaxResponse.setData(JSON.toJSON(usr));
        }
        return ajaxResponse.success();
    }

    @PostMapping("/queryUserByUid")
    public AjaxResponse queryUserByUuid(
            @NotBlank(message = "UUID不能为空!")
                    String uuid) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        MyUserDetail usr = userServices.selectByUUID(uuid);
        if (usr != null) {
            ajaxResponse.setData(JSON.toJSON(usr));
        }
        return ajaxResponse.success();
    }
    /**
     * 修改密码，x-www-form请求
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
    @TestUserForbidden
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
        int rs = userServices.updatePassWordByUUID(newUsr);
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
     * @return 返回头像新地址
     * @throws UploadFileException
     */
    @PostMapping("/editPhoto")
    public AjaxResponse editPhoto(MultipartFile file) throws UploadFileException {
        AjaxResponse response = new AjaxResponse();
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        boolean checkType = UploadUtils.checkFileType(file, FileType.image);
        if (!checkType) {
            return response.error().setCode(501).setMessage("不允许上传该类型文件!");
        }
        MyUserDetail usr = userServices.selectByUUID(uuid);
        String photoUrl= usr.getPhotourl();
        String suffixName = ".jpg";
        String photoName = UUID.randomUUID().toString() + suffixName;
        if (!photoUrl.contains(FileConst.DEFAULT_PHOTO_URL)){
            photoName= photoUrl.substring(photoUrl.lastIndexOf("/") + 1);
        }

        //上传新头像
        String fileName = file.getOriginalFilename();
        String uploadPath = FileConst.LOCAL_PHOTO_PATH + photoName ;
        String fileUrl = FileConst.Photo_BASE_PATH + uploadPath;
        //真实保存地址
        String realPath = UploadUtils.uploadFile(file, uploadPath, FileType.image);
        //存入数据库
        MyUserDetail newInfo = new MyUserDetail();
        newInfo.setPhotourl(fileUrl);
        newInfo.setUuid(uuid);
        userServices.updateByUUIDSelective(newInfo);
        //压缩头像
        UserFileDetail fileDetail = new UserFileDetail();
        fileDetail.setUploadTime(new Date());
        fileDetail.setFileName(fileName);
        fileDetail.setFilePath(realPath);
        fileDetail.setUuid(uuid);
        fileDetail.setFileType(FileType.image.toString());
        //fileDetailDao.insertSelective(fileDetail);
        //发送图片压缩事件,
        ApplicationEvent event = new ImagePressEvent(fileDetail);
        applicationContext.publishEvent(event);

        usr.setPhotourl(fileUrl);
        response.success().setMessage("修改成功！").setData(JSON.toJSON(usr));//返回更新后的数据
        return response;
    }


    /**
     * 按需更改基本信息
     * 传入参数不为空则修改
     *
     * @param user
     * @return 返回本人新资料
     */
    @PostMapping("/editInfo")
    public AjaxResponse editUserInfo(@RequestBody @Valid UserEditVo user) {
        AjaxResponse response = new AjaxResponse();
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        user.setUuid(uuid);

        MyUserDetail userDetail = new MyUserDetail();
        BeanUtils.copyProperties(user, userDetail);
        int res = userServices.updateByUUIDSelective(userDetail);
        if (res > 0) {
            MyUserDetail NewUsr = userServices.selectByUUID(uuid);
            return response.success().setData(JSON.toJSON(NewUsr));
        }
        return response.error().setCode(501).setMessage("资料更新失败！");
    }

}
