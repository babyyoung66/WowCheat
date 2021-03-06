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
        return ajaxResponse.error().setMessage("??????????????????????????????????????????????????????");
    }

    /**
     * ??????wowId????????????
     *
     * @param wowId x-www-form????????????
     * @return ?????????????????????????????????????????????
     */

    @PostMapping("/queryUser")
    public AjaxResponse queryUserByWowId(
                                         @NotBlank(message = "Wow???????????????!")
                                         @Pattern(regexp = "[a-zA-Z0-9]*", message = "Wow??????????????????????????????????????????!")
                                         @Length(min = 5, max = 18, message = "Wow?????????5-18???!")
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
            @NotBlank(message = "UUID????????????!")
                    String uuid) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        MyUserDetail usr = userServices.selectByUUID(uuid);
        if (usr != null) {
            ajaxResponse.setData(JSON.toJSON(usr));
        }
        return ajaxResponse.success();
    }
    /**
     * ???????????????x-www-form??????
     * ???????????????
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
    public AjaxResponse EditPassword(@NotBlank(message = "???????????????!")
                                     @Pattern(regexp = "[a-zA-Z0-9.!@#$%^&_;~,?]*?", message = "??????????????????????????????,?????????????????????????????????: .!@#$%^&_;~,?")
                                     @Length(min = 6, max = 18, message = "????????????6-18???!")
                                             String password,
                                     String oldPassword) {
        AjaxResponse response = new AjaxResponse();
        //???security?????????????????????uuid??????????????????????????????
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        BCryptPasswordEncoder bcry = new BCryptPasswordEncoder();
        MyUserDetail old = userServices.selectByUUID(uuid);
        boolean success = bcry.matches(oldPassword, old.getPassword());
        if (!success) {
            response.error().setMessage("?????????????????????");
            return response;
        }
        MyUserDetail newUsr = new MyUserDetail();
        newUsr.setUuid(uuid);
        newUsr.setPassword(password);
        int rs = userServices.updatePassWordByUUID(newUsr);
        if (rs > 0) {
            response.success().setMessage("?????????????????????????????????");
            ApplicationEvent event = new UserChangeEvent(newUsr);
            applicationContext.publishEvent(event); //?????????????????????????????????
            return response;
        }
        return response.error().setMessage("????????????????????????????????????");
    }


    /**
     * ????????????
     *
     * @param name
     * @return ???????????????
     */
    @PostMapping("editName")
    public AjaxResponse changeName(@NotBlank(message = "??????????????????!")
                                   @Pattern(regexp = "[^<>#^\\r\\t\\n*&\\\\/$]*?", message = "??????????????????????????????!")
                                   @Length(min = 1, max = 12, message = "????????????1-12???!")
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
            return response.success().setMessage("???????????????").setData(JSON.toJSON(detail));
        }
        return response.error().setMessage("?????????????????????????????????");
    }

    /**
     * ????????????
     *
     * @param file
     * @return ?????????????????????
     * @throws UploadFileException
     */
    @PostMapping("/editPhoto")
    public AjaxResponse editPhoto(MultipartFile file) throws UploadFileException {
        AjaxResponse response = new AjaxResponse();
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        boolean checkType = UploadUtils.checkFileType(file, FileType.image);
        if (!checkType) {
            return response.error().setCode(501).setMessage("??????????????????????????????!");
        }
        MyUserDetail usr = userServices.selectByUUID(uuid);
        String photoUrl= usr.getPhotourl();
        String suffixName = ".jpg";
        String photoName = UUID.randomUUID().toString() + suffixName;
        if (!photoUrl.contains(FileConst.DEFAULT_PHOTO_URL)){
            photoName= photoUrl.substring(photoUrl.lastIndexOf("/") + 1);
        }

        //???????????????
        String fileName = file.getOriginalFilename();
        String uploadPath = FileConst.LOCAL_PHOTO_PATH + photoName ;
        String fileUrl = FileConst.Photo_BASE_PATH + uploadPath;
        //??????????????????
        String realPath = UploadUtils.uploadFile(file, uploadPath, FileType.image);
        //???????????????
        MyUserDetail newInfo = new MyUserDetail();
        newInfo.setPhotourl(fileUrl);
        newInfo.setUuid(uuid);
        userServices.updateByUUIDSelective(newInfo);
        //????????????
        UserFileDetail fileDetail = new UserFileDetail();
        fileDetail.setUploadTime(new Date());
        fileDetail.setFileName(fileName);
        fileDetail.setFilePath(realPath);
        fileDetail.setUuid(uuid);
        fileDetail.setFileType(FileType.image.toString());
        //fileDetailDao.insertSelective(fileDetail);
        //????????????????????????,
        ApplicationEvent event = new ImagePressEvent(fileDetail);
        applicationContext.publishEvent(event);

        usr.setPhotourl(fileUrl);
        response.success().setMessage("???????????????").setData(JSON.toJSON(usr));//????????????????????????
        return response;
    }


    /**
     * ????????????????????????
     * ??????????????????????????????
     *
     * @param user
     * @return ?????????????????????
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
        return response.error().setCode(501).setMessage("?????????????????????");
    }

}
