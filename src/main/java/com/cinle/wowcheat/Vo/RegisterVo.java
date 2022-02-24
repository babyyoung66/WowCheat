package com.cinle.wowcheat.Vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;

/**
 * @Author JunLe
 * @Time 2022/2/21 16:35
 *
 */
@ApiModel(description = "注册类实体")
public class RegisterVo  {


    @ApiModelProperty(value = "用户自定义id，唯一且可修改，限制5-18位")
    @Pattern(regexp = "[a-zA-Z0-9]*",
            message = "Wow号不能包含中文字符及特殊符号!")
    @Length(min = 5,max = 18,message = "Wow号长度5-18位!")
    private String wowId;

    @ApiModelProperty(value = "用户邮箱，唯一值，可修改，不为空")
    @Email(message = "邮箱格式错误!")
    private String email;

    @ApiModelProperty(value = "邮箱验证码")
    private String code;



    public String getWowId() {
        return wowId;
    }

    public void setWowId(String wowId) {
        this.wowId = wowId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String Email) {
        this.email = Email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String Code) {
        this.code = Code;
    }

    @Override
    public String toString() {
        return "RegisterVo{" +
                "wowId='" + wowId + '\'' +
                ", email='" + email + '\'' +
                ", Code='" + code + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterVo that = (RegisterVo) o;
        return Objects.equals(wowId, that.wowId) &&
                Objects.equals(email, that.email) &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wowId, email, code);
    }
}
