package com.cinle.wowcheat.Model;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * wow_friends
 * @author 
 */
public class Friends implements Serializable {
    private Integer autoId;

    /**
     * 用户uuid
     */
    @NotBlank(message = "自身uuid不能为空！")
    private String sUuid;

    /**
     * 朋友uuid
     */
    @NotBlank(message = "好友uuid不为空！")
    private String fUuid;

    /**
     * 好友状态（1正常，2屏蔽，3拉黑）默认1
     */
    private Integer status;

    private static final long serialVersionUID = 1L;

    public Integer getAutoId() {
        return autoId;
    }

    public void setAutoId(Integer autoId) {
        this.autoId = autoId;
    }

    public String getsUuid() {
        return sUuid;
    }

    public void setsUuid(String sUuid) {
        this.sUuid = sUuid;
    }

    public String getfUuid() {
        return fUuid;
    }

    public void setfUuid(String fUuid) {
        this.fUuid = fUuid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Friends{" +
                "autoId=" + autoId +
                ", sUuid='" + sUuid + '\'' +
                ", fUuid='" + fUuid + '\'' +
                ", status=" + status +
                '}';
    }
}