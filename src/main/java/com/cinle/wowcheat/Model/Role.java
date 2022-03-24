package com.cinle.wowcheat.Model;

import com.cinle.wowcheat.Enum.RoleEnum;

import java.io.Serializable;

/**
 * wow_role
 * @author 权限表
 */
public class Role implements Serializable {
    private Integer autoId;

    private String userUuid;

    private RoleEnum role;

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

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum Role) {
        this.role = Role;
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