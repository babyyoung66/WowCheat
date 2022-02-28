package com.cinle.wowcheat.Security;

import com.alibaba.fastjson.annotation.JSONField;
import com.cinle.wowcheat.Model.MyUserDetail;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

/**
 * @Author JunLe
 * @Time 2022/2/20 18:10
 */
public class CustomerUserDetails extends MyUserDetail implements UserDetails {

    public CustomerUserDetails(){
        super();
    }
    /**
     * security字段，直接继承实现，防止污染数据库实体类
     * */

    @JSONField(serialize=false)
    boolean accountNonExpired;//是否没过期
    @JSONField(serialize=false)
    boolean accountNonLocked;//是否没被锁定
    @JSONField(serialize=false)
    boolean credentialsNonExpired;//是否没过期
    @JSONField(serialize=false)
    boolean enabled;//账号是否可用

    @JSONField(serialize=false)
    boolean rememberMe;
    @JSONField(serialize=false)
    String verifyCode; //验证码
    //不序列化
    @JSONField(serialize=false)
    Collection<? extends GrantedAuthority> authorities;//用户的权限集合

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
    @Override
    public String getUsername() {
        return super.getWowId();
    }
    @Override
    public String getPassword() {
        return super.getPassword();
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        /* 根据自定义的状态*/
        return super.getStatus() != 2;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    @Override
    public String toString() {
        return "UserLoginVo{" +
                super.toString() +
                "accountNonExpired=" + accountNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", enabled=" + enabled +
                ", authorities=" + authorities +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        CustomerUserDetails  that = (CustomerUserDetails) o;
        return Objects.equals(getPassword() , that.getUsername())&&
                Objects.equals(getPassword() , that.getPassword()) ;
    }

    @Override
    public int hashCode() {
       return   Objects.hash(getUsername(),getPassword());
    }
}
