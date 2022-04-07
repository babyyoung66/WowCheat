package com.cinle.wowcheat.Model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * wow_friends_request
 * @author junle
 * 好友请求实体
 */
@ApiModel(description="好友请求实体")
public class FriendsRequest implements Serializable {
    private Integer autoId;

    /**
     * 请求者uuid
     */
    @ApiModelProperty(value="请求者uuid")
    private String requestUuid;

    /**
     * 接受者uuid
     */
    @ApiModelProperty(value="接受者uuid")
    @NotBlank(message = "uuid不能为空!")
    private String receiverUuid;

    /**
     * 请求信息
     */
    @ApiModelProperty(value="请求信息")
    @Length(max = 48,message = "最长48字符！")
    @Pattern(regexp = "[^<>#^\\r\\t\\n*&\\\\/$]*?",
            message = "不能包含特殊符号!")
    private String requestMessage;

    /**
     * 请求时间
     */
    private Date requestTime;

    /**
     * 请求状态（只能由接收者更改，0等待验证中，1已通过验证，2已拒绝）
     */
    @ApiModelProperty(value="请求状态（只能由接收者更改，0等待验证中，1已通过验证，2已拒绝）",example = "1")
    @Range(min = 0,max = 2,message = "requestStatus格式错误！" )
    private Integer requestStatus;

    /**
     * 接受者或请求者信息
     * 多表查询
     */
    private MyUserDetail userInfo;

    private static final long serialVersionUID = 1L;

    public Integer getAutoId() {
        return autoId;
    }

    public void setAutoId(Integer autoId) {
        this.autoId = autoId;
    }

    public String getRequestUuid() {
        return requestUuid;
    }

    public void setRequestUuid(String requestUuid) {
        this.requestUuid = requestUuid;
    }

    public String getReceiverUuid() {
        return receiverUuid;
    }

    public void setReceiverUuid(String receiverUuid) {
        this.receiverUuid = receiverUuid;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Integer getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Integer requestStatus) {
        this.requestStatus = requestStatus;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public MyUserDetail getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(MyUserDetail userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "FriendsRequest{" +
                "autoId=" + autoId +
                ", requestUuid='" + requestUuid + '\'' +
                ", receiverUuid='" + receiverUuid + '\'' +
                ", requestMessage='" + requestMessage + '\'' +
                ", requestTime=" + requestTime +
                ", requestStatus=" + requestStatus +
                ", userInfo=" + userInfo +
                '}';
    }
}