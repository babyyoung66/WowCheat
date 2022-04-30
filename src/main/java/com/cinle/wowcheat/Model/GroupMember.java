package com.cinle.wowcheat.Model;

import com.alibaba.fastjson.annotation.JSONField;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.util.Date;

/**
 * wow_groups_member
 * @author 
 */
public class GroupMember implements Serializable {
    private Integer autoId;

    /**
     * 群组uuid
     */
    private String groupUuid;

    /**
     * 用户uuid
     */
    private String userUuid;

    /**
     * 群备注
     */
    private String remarks;

    /**
     * 加入时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date joinTime;

    /**
     * 邀请者uuid
     */
    private String inviterUuid;

    /**
     * 成员权限，0群众，1管理员，默认0
     */
    @Range(min = 0,max = 1,message = "成员权限格式错误！")
    private Integer memberRole;

    /**
     * 成员状态，0正常，1禁言,默认0
     */
    @Range(min = 0,max = 1,message = "成员状态格式错误！")
    private Integer memberStatus;

    /**
     * 上次打开聊天框的时间
     */
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

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public String getInviterUuid() {
        return inviterUuid;
    }

    public void setInviterUuid(String inviterUuid) {
        this.inviterUuid = inviterUuid;
    }

    public Integer getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(Integer memberRole) {
        this.memberRole = memberRole;
    }

    public Integer getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(Integer memberStatus) {
        this.memberStatus = memberStatus;
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
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        GroupMember other = (GroupMember) that;
        return (this.getAutoId() == null ? other.getAutoId() == null : this.getAutoId().equals(other.getAutoId()))
            && (this.getGroupUuid() == null ? other.getGroupUuid() == null : this.getGroupUuid().equals(other.getGroupUuid()))
            && (this.getUserUuid() == null ? other.getUserUuid() == null : this.getUserUuid().equals(other.getUserUuid()))
            && (this.getJoinTime() == null ? other.getJoinTime() == null : this.getJoinTime().equals(other.getJoinTime()))
            && (this.getInviterUuid() == null ? other.getInviterUuid() == null : this.getInviterUuid().equals(other.getInviterUuid()))
            && (this.getMemberRole() == null ? other.getMemberRole() == null : this.getMemberRole().equals(other.getMemberRole()))
            && (this.getMemberStatus() == null ? other.getMemberStatus() == null : this.getMemberStatus().equals(other.getMemberStatus()))
            && (this.getLastCheatTime() == null ? other.getLastCheatTime() == null : this.getLastCheatTime().equals(other.getLastCheatTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAutoId() == null) ? 0 : getAutoId().hashCode());
        result = prime * result + ((getGroupUuid() == null) ? 0 : getGroupUuid().hashCode());
        result = prime * result + ((getUserUuid() == null) ? 0 : getUserUuid().hashCode());
        result = prime * result + ((getJoinTime() == null) ? 0 : getJoinTime().hashCode());
        result = prime * result + ((getInviterUuid() == null) ? 0 : getInviterUuid().hashCode());
        result = prime * result + ((getMemberRole() == null) ? 0 : getMemberRole().hashCode());
        result = prime * result + ((getMemberStatus() == null) ? 0 : getMemberStatus().hashCode());
        result = prime * result + ((getLastCheatTime() == null) ? 0 : getLastCheatTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", autoId=").append(autoId);
        sb.append(", groupUuid=").append(groupUuid);
        sb.append(", userUuid=").append(userUuid);
        sb.append(", remarks=").append(remarks);
        sb.append(", joinTime=").append(joinTime);
        sb.append(", inviterUuid=").append(inviterUuid);
        sb.append(", memberRole=").append(memberRole);
        sb.append(", memberStatus=").append(memberStatus);
        sb.append(", lastCheatTime=").append(lastCheatTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}