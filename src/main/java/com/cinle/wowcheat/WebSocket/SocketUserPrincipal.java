package com.cinle.wowcheat.WebSocket;
import java.security.Principal;
import java.util.Date;
import java.util.List;


/**
 * @Author JunLe
 * @Time 2022/3/22 20:33
 * 连接主体
 */
public class SocketUserPrincipal implements Principal {

    /**
     * 记录token，后续验证时效性
     */
    private String token;

    /**
     * 连接者uuid
     * token中获取
     */
    private String name;

    /**
     * 连接用户的权限
     * token中获取
     */
    private List<String> roles;

    /**
     * 上次发送时间
     */
    private Date lastSendTime;

    /**
     * 间隔时间内的消息记录数
     */
    private int limitTotal;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }


    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Date getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(Date lastSend) {
        this.lastSendTime = lastSend;
    }

    public int getLimitTotal() {
        return limitTotal;
    }

    public void setLimitTotal(int limitTotal) {
        this.limitTotal = limitTotal;
    }

    @Override
    public String toString() {
        return "SocketUserPrincipal{" +
                "token='" + token + '\'' +
                ", name='" + name + '\'' +
                ", roles=" + roles +
                ", lastSendTime=" + lastSendTime +
                ", limitTotal=" + limitTotal +
                '}';
    }
}
