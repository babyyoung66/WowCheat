package com.cinle.wowcheat.Vo;

import java.io.Serializable;

/**
 * @Author JunLe
 * @Time 2022/5/4 20:42
 *
 * 邀请加入群聊反馈内容，
 * success为true的则socket推送至每个在线成员
 * 为false的则http返回给邀请者
 */
public class InviteMemberVo implements Serializable {

    private boolean success;
    /**
     * 被邀请者uuid
     */
    private String uuid;
    /**
     * 被邀请者名称
     */
    private String name;
    /**
     * 邀请者名称
     */
    private String invitorName;

    private String message;

    public boolean isSuccess() {
        return success;
    }

    public InviteMemberVo setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public InviteMemberVo setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getName() {
        return name;
    }

    public InviteMemberVo setName(String name) {
        this.name = name;
        return this;
    }

    public String getInvitorName() {
        return invitorName;
    }

    public InviteMemberVo setInvitorName(String invitorName) {
        this.invitorName = invitorName;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public InviteMemberVo setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "InviteMemberVo{" +
                "success=" + success +
                ", uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", invitorName='" + invitorName + '\'' +
                ", errorMessage='" + message + '\'' +
                '}';
    }
}
