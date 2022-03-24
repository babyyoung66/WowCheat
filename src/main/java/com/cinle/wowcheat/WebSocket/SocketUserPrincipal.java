package com.cinle.wowcheat.WebSocket;
import java.security.Principal;
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

    @Override
    public String toString() {
        return "SocketUserPrincipal{" +
                "token='" + token + '\'' +
                ", name='" + name + '\'' +
                ", roles=" + roles +
                '}';
    }
}
