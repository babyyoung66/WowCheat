package com.cinle.wowcheat.Model;

import com.alibaba.fastjson.annotation.JSONField;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

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
    private String fremarks;

    /**
     * 好友状态（1正常，2屏蔽，3拉黑，4被对方删除）默认1,4只能删除时由系统设置
     */
    @Range(min = 0,max = 3,message = "好友状态格式错误！！")
    private Integer fstatus;

    /**
     * 上次联系时间
     * */
    @JSONField(format="yyyy-MM-dd HH:mm:ss.SSS")
    private Date lastCheatTime;

    /**
     * 未读记录数
     * 数据库不含该字段
     * */
    @JSONField(deserialize = false)
    private long unReadTotal;

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
        return fremarks;
    }

    public void setRemarks(String remarks) {
        this.fremarks = remarks;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getStatus() {
        return fstatus;
    }

    public void setStatus(Integer status) {
        this.fstatus = status;
    }


    public Date getLastCheatTime() {
        return lastCheatTime;
    }

    public void setLastCheatTime(Date lastCheatTime) {
        this.lastCheatTime = lastCheatTime;
    }

    public long getUnReadTotal() {
        return unReadTotal;
    }

    public void setUnReadTotal(long unReadTotal) {
        this.unReadTotal = unReadTotal;
    }

    @Override
    public String toString() {
        return "Friends{" +
                "autoId=" + autoId +
                ", sUuid='" + sUuid + '\'' +
                ", fUuid='" + fUuid + '\'' +
                ", fremarks='" + fremarks + '\'' +
                ", fstatus=" + fstatus +
                ", lastCheatTime=" + lastCheatTime +
                ", unReadTotal=" + unReadTotal +
                '}';
    }
}