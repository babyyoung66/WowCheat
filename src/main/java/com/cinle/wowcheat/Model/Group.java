package com.cinle.wowcheat.Model;

import com.alibaba.fastjson.annotation.JSONField;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * wow_groups
 * @author junle
 */
public class Group implements Serializable {
    private Integer autoId;

    /**
     * 群唯一标识
     */
    private String uuid;

    /**
     * 群名称
     */
    @Pattern(regexp = "[^<>#^\\r\\t\\n*&\\\\/$]*?",
            message = "群组名称不能包含特殊符号!")
    private String name;

    /**
     * 创建时间
     */
    @JSONField(format="yyyy-MM-dd")
    private Date createTime;

    /**
     * 创建人uuid
     */
    private String creatorUuid;

    /**
     * 群组头像url
     */
    private String photourl;

    /**
     * 状态，0正常，1禁言，2封禁，默认0
     * 用户只能更改0或1
     * 状态2只能由后台管理员更改
     */
    @Range(min = 0,max = 1,message = "群组状态格式错误！！")
    private Integer groupStatus;

    /**
     * 成员数量
     */
    private Integer memberTotal;

    /**
     * 管理员数量
     */
    private Integer adminTotal;

    /**
     * 管理员id列表
     * */
    private List<String> adminIds;

    /**
     * 群组所有用户id
     */
    private List<String> memberIds;

    /**
     * 个人与群聊的信息
     * 登录时查询用到
     */
    private GroupMember concatInfo;

    private static final long serialVersionUID = 1L;

    public Integer getAutoId() {
        return autoId;
    }

    public void setAutoId(Integer autoId) {
        this.autoId = autoId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreatorUuid() {
        return creatorUuid;
    }

    public void setCreatorUuid(String creatorUuid) {
        this.creatorUuid = creatorUuid;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public Integer getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(Integer groupStatus) {
        this.groupStatus = groupStatus;
    }

    public Integer getMemberTotal() {
        return memberTotal;
    }

    public void setMemberTotal(Integer memberTotal) {
        this.memberTotal = memberTotal;
    }

    public Integer getAdminTotal() {
        return adminTotal;
    }

    public void setAdminTotal(Integer adminTotal) {
        this.adminTotal = adminTotal;
    }


    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public List<String> getAdminIds() {
        return adminIds;
    }

    public void setAdminIds(List<String> adminIds) {
        this.adminIds = adminIds;
    }

    public GroupMember getConcatInfo() {
        return concatInfo;
    }

    public void setConcatInfo(GroupMember concatInfo) {
        this.concatInfo = concatInfo;
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
        Group other = (Group) that;
        return (this.getAutoId() == null ? other.getAutoId() == null : this.getAutoId().equals(other.getAutoId()))
            && (this.getUuid() == null ? other.getUuid() == null : this.getUuid().equals(other.getUuid()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getCreatorUuid() == null ? other.getCreatorUuid() == null : this.getCreatorUuid().equals(other.getCreatorUuid()))
            && (this.getPhotourl() == null ? other.getPhotourl() == null : this.getPhotourl().equals(other.getPhotourl()))
            && (this.getGroupStatus() == null ? other.getGroupStatus() == null : this.getGroupStatus().equals(other.getGroupStatus()))
            && (this.getMemberTotal() == null ? other.getMemberTotal() == null : this.getMemberTotal().equals(other.getMemberTotal()))
            && (this.getAdminTotal() == null ? other.getAdminTotal() == null : this.getAdminTotal().equals(other.getAdminTotal()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAutoId() == null) ? 0 : getAutoId().hashCode());
        result = prime * result + ((getUuid() == null) ? 0 : getUuid().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getCreatorUuid() == null) ? 0 : getCreatorUuid().hashCode());
        result = prime * result + ((getPhotourl() == null) ? 0 : getPhotourl().hashCode());
        result = prime * result + ((getGroupStatus() == null) ? 0 : getGroupStatus().hashCode());
        result = prime * result + ((getMemberTotal() == null) ? 0 : getMemberTotal().hashCode());
        result = prime * result + ((getAdminTotal() == null) ? 0 : getAdminTotal().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("autoId=").append(autoId);
        sb.append(", uuid=").append(uuid);
        sb.append(", name=").append(name);
        sb.append(", createTime=").append(createTime);
        sb.append(", creatorUuid=").append(creatorUuid);
        sb.append(", photourl=").append(photourl);
        sb.append(", groupStatus=").append(groupStatus);
        sb.append(", memberTotal=").append(memberTotal);
        sb.append(", adminTotal=").append(adminTotal);
        sb.append(", memberIds=").append(memberIds);
        sb.append(", adminIds=").append(adminIds);
        sb.append(", concatInfo=").append(concatInfo);
        sb.append("]");
        return sb.toString();
    }
}