package com.cinle.wowcheat.Model;

import java.io.Serializable;

/**
 * wow_role
 * @author 权限表
 */
public class Role implements Serializable {
    private Integer autoId;

    private String userUuid;

    private Integer role;

    private static final long serialVersionUID = 1L;

    public Integer getAutoId() {
        return autoId;
    }

    public void setAutoId(Integer autoId) {
        this.autoId = autoId;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "WowRole{" +
                "autoId=" + autoId +
                ", userUuid='" + userUuid + '\'' +
                ", role=" + role +
                '}';
    }
}