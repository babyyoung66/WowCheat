package com.cinle.wowcheat.Model;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * wow_userdetail
 * @author 
 */
@ApiModel(description = "用户基本信息实体")
public class MyUserDetail implements Serializable {
    @ApiModelProperty(value = "数据库自增主键",readOnly = true)//post请求不显示
    private Integer autoId;

    @ApiModelProperty(value = "用户uuid，唯一不可变",readOnly = true)//post请求不显示
    private String uuid;


    @ApiModelProperty(value = "用户自定义id，唯一且可修改，限制5-18位")
    @NotBlank(message = "Wow号不能为空!")
    @Pattern(regexp = "[a-zA-Z0-9]*",
            message = "Wow号不能包含中文字符及特殊符号!")
    @Length(min = 5,max = 18,message = "Wow号长度5-18位!")
    private String wowId;

    @ApiModelProperty(value = "用户昵称，限制1-12位" )
    @NotBlank(message = "昵称不能为空!")
    @Pattern(regexp = "[^<>#^\\r\\t\\n*&\\\\/$]*?",
            message = "昵称不能包含特殊符号!")
    @Length(min = 1,max = 12,message = "昵称长度1-12位!")
    private String name;


    @ApiModelProperty(value = "用户密码，限制6-18位" )
    @JSONField(serialize=false) //不序列化密码
    @NotBlank(message = "密码不能空") // 密码只能字母和数字以及.!@#$%^&_;~,? 这几个字符
    @Pattern(regexp = "[a-zA-Z0-9.!@#$%^&_;~,?]*?",
            message = "密码不能包含中文字符,且只能包含以下特殊字符: .!@#$%^&_;~,?")
    @Length(min = 6,max = 18,message = "密码长度6-18位!")
    private String password;

    @ApiModelProperty(value = "性别，0代表女，1代表男，3代表隐藏，默认3")
    private Integer sex;

    @ApiModelProperty(value = "用户头像链接")
    private String photourl;

    @ApiModelProperty(value = "用户手机号")
    @Pattern(regexp = "^1[3|4|5|7|8|9][0-9]\\d{8}$ ",message = "手机号格式错误!")
    private String phonenum;


    @ApiModelProperty(value = "用户邮箱，唯一值，可修改，不为空")
    @NotBlank(message = "请输入邮箱!")
    @Email(message = "邮箱格式错误!")
    private String email;

    @ApiModelProperty(value = "用户生日")
    private String birthday;

    @ApiModelProperty(value = "用户地址")
    private String address;

    @ApiModelProperty(value = "用户注册时间",readOnly = true)
    private LocalDateTime creattime;

    @ApiModelProperty(value = "账号状态，1正常，2限制登录")
    private Integer status;

    private Friends friendsInfo;

    private static final long serialVersionUID = 1L;

    public Integer getAutoId() {
        return autoId;
    }

    public void setAutoId(Integer autoId) {
        this.autoId = autoId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getWowId() {
        return wowId;
    }

    public void setWowId(String wowId) {
        this.wowId = wowId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreattime() {
        return creattime;
    }

    public void setCreattime(LocalDateTime creattime) {
        this.creattime = creattime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Friends getFriendsInfo() {
        return friendsInfo;
    }

    public void setFriendsInfo(Friends friendsInfo) {
        this.friendsInfo = friendsInfo;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return "MyUserDetail{" +
                "autoId=" + autoId +
                ", uuid='" + uuid + '\'' +
                ", wowId='" + wowId + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", sex=" + sex +
                ", photourl='" + photourl + '\'' +
                ", phonenum='" + phonenum + '\'' +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                ", address='" + address + '\'' +
                ", creattime=" + creattime +
                ", status=" + status +
                ", friendsInfo=" + friendsInfo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyUserDetail)) return false;
        MyUserDetail that = (MyUserDetail) o;
        return Objects.equals(getAutoId(), that.getAutoId()) &&
                Objects.equals(getUuid(), that.getUuid()) &&
                Objects.equals(getWowId(), that.getWowId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getPassword(), that.getPassword()) &&
                Objects.equals(getSex(), that.getSex()) &&
                Objects.equals(getPhotourl(), that.getPhotourl()) &&
                Objects.equals(getPhonenum(), that.getPhonenum()) &&
                Objects.equals(getEmail(), that.getEmail()) &&
                Objects.equals(getBirthday(), that.getBirthday()) &&
                Objects.equals(getAddress(), that.getAddress()) &&
                Objects.equals(getCreattime(), that.getCreattime()) &&
                Objects.equals(getStatus(), that.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAutoId(), getUuid(), getWowId(), getName(), getPassword(), getSex(), getPhotourl(), getPhonenum(), getEmail(), getBirthday(), getAddress(), getCreattime(), getStatus());
    }
}