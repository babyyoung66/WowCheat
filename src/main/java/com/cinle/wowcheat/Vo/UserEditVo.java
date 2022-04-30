package com.cinle.wowcheat.Vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.util.Date;


/**
 * @Author JunLe
 * @Time 2022/4/3 1:26
 * 用户信息修改实体
 */
@ApiModel(description = "用户基础信息修改实体")
public class UserEditVo {
    @ApiModelProperty(value = "用户唯一标识，后台认证体获取",readOnly = true)
    private String uuid;

    @ApiModelProperty(value = "用户昵称，限制1-18位" )
    @NotBlank(message = "昵称不能为空!")
    @Pattern(regexp = "[^<>#^\\r\\t\\n*&\\\\/$]*?",
            message = "昵称不能包含特殊符号!")
    @Length(min = 1,max = 18,message = "昵称长度1-18位!")
    private String name;


    @ApiModelProperty(value = "性别，0代表女，1代表男，2代表隐藏，默认2",example = "1")
    @Range(min = 0,max = 2,message = "性别格式错误！")
    private Integer sex;

    @ApiModelProperty(value = "用户头像链接")
    private String photourl;


    @ApiModelProperty(value = "用户生日")
    @JSONField(format="yyyy-MM-dd")
    @Past(message = "生日只能是一个过去的时间！")
    private Date birthday;

    @ApiModelProperty(value = "用户地址")
    @Length(max = 48,message = "地区不超过48字符！")
    private String address;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }



    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "UserEditVo{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", photourl='" + photourl + '\'' +
                ", birthday=" + birthday +
                ", address='" + address + '\'' +
                '}';
    }
}
