package com.cinle.wowcheat.AOP;

import com.cinle.wowcheat.Enum.RoleEnum;
import com.cinle.wowcheat.Utils.SecurityContextUtils;
import com.cinle.wowcheat.Vo.AjaxResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @Author JunLe
 * @Time 2022/4/30 19:11
 * 拦截测试用户的请求禁止修改一些必要信息
 */
@Component
@Aspect
public class TestRoleInterceptor {

    @Pointcut("@annotation(com.cinle.wowcheat.AOP.TestUserForbidden)")
    public void cut(){}


    @Around(value = "cut()")
    public Object checkTestUser(ProceedingJoinPoint joinPoint) throws Throwable {
        SimpleGrantedAuthority testRole = new SimpleGrantedAuthority(RoleEnum.TEST.toString());
        Collection<SimpleGrantedAuthority> authorities  = (Collection<SimpleGrantedAuthority>) SecurityContextUtils.getCurrentUserAuthentication();
        if (authorities.contains(testRole)){
            AjaxResponse ajaxResponse = new AjaxResponse();
            ajaxResponse.error().setCode(403).setMessage("测试用户禁止修改！");
            return ajaxResponse;
        }else {
            return joinPoint.proceed();
        }
    }


}
