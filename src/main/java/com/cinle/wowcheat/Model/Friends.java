package com.cinle.wowcheat.Model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
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
    private String sUuid;

    /**
     * 朋友uuid
     */
    @NotBlank(message = "好友uuid不为空！")
    private String fUuid;

    /**
     * 好友备注
     */
    @Length(max = 12,message = "备注长度0-12位!")
    @Pattern(regexp = "[^<>#^\\r\\t\\n*&\\\\/$]*?",
            message = "备注不能包含特殊符号!")
    private String fRemarks;

    /**
     * 好友状态（1正常，2屏蔽，3拉黑，4被对方删除）默认1
     */
    private Integer fStatus;

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

    public String getRemarks() {
        return fRemarks;
    }

    public void setRemarks(String remarks) {
        this.fRemarks = remarks;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getStatus() {
        return fStatus;
    }

    public void setStatus(Integer status) {
        this.fStatus = status;
    }

    @Override
    public String toString() {
        return "Friends{" +
                "autoId=" + autoId +
                ", sUuid='" + sUuid + '\'' +
                ", fUuid='" + fUuid + '\'' +
                ", Fremarks='" + fRemarks + '\'' +
                ", Fstatus=" + fStatus +
                '}';
    }
}