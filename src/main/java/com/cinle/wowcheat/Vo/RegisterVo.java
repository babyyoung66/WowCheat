package com.cinle.wowcheat.Vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

/**
 * @Author JunLe
 * @Time 2022/2/21 16:35
 *
 */
@ApiModel(description = "注册类实体")
public class RegisterVo  {

    @ApiModelProperty(value = "邮箱验证码")
    private String code;

    @ApiModelProperty(value = "用户自定义id，唯一且可修改，限制5-18位")
    @Pattern(regexp = "[a-zA-Z0-9]*",
            message = "Wow号不能包含中文字符及特殊符号!")
    @Length(min = 5,max = 18,message = "Wow号长度5-18位!")
    private String wowId;


    @ApiModelProperty(value = "用户邮箱，唯一值，可修改，不为空")
    @Email(message = "邮箱格式错误!")
    private String email;



    public String getWowId() {
        return wowId;
    }

    public void setWowId(String wowId) {
        this.wowId = wowId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        code = code;
    }

    @Override
    public String toString() {
        return "RegisterVo{" +
                "wowId='" + wowId + '\'' +
                ", email='" + email + '\'' +
                ", Code='" + code + '\'' +
                '}';
    }
}
