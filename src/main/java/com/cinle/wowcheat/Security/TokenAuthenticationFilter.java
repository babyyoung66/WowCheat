package com.cinle.wowcheat.Security;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Enum.RoleEnum;
import com.cinle.wowcheat.Exception.TokenException;
import com.cinle.wowcheat.Vo.AjaxResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author JunLe
 * @Time 2022/2/25 0:11
 * 验证token
 */
public class TokenAuthenticationFilter extends BasicAuthenticationFilter {

    private JwtTokenService jwtTokenService;

    public TokenAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }


    /**
     * @param
     * @return
     */
    public TokenAuthenticationFilter setService(JwtTokenService TokenService) {
        this.jwtTokenService = TokenService;
        return this;
    }

    /**
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        UsernamePasswordAuthenticationToken authenticationToken = null;
        try {
            authenticationToken = getAuthentication(request, response);
        } catch (Exception e) {
            //e.printStackTrace();
            response.setStatus(401);
            AjaxResponse ajaxResponse = new AjaxResponse();
            ajaxResponse.error().setMessage(  e.getMessage()).setCode(401);
            PrintWriter out = response.getWriter();
            out.println(JSON.toJSONString(ajaxResponse));
            out.flush();
            out.close();
            return;
        }
        if (authenticationToken != null) {
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);;
        }
            /** 无论是否认证成功
             * 交给security的过滤链处理，不然就自己校验URL、做无权限处理
             * 由 FilterSecurityInterceptor 最后决定是否通过
             * 该过滤器是SpringSecurity最终决定是否放行的过滤器
             * */
        chain.doFilter(request, response);
//        super.doFilterInternal(request, response, chain);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException, TokenException {
        response.setContentType("application/json;charset=UTF-8");
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            //为空则尝试从parameter获取
            token = request.getParameter("token");
        }

        if (StringUtils.hasText(token)) {
            //验证是否存在、是否过期，不正常则抛异常
            jwtTokenService.CheckToken(token);

            //是否需要刷新token
            String newToken = jwtTokenService.isNeedFlushToken(token);
            if (StringUtils.hasText(newToken)) {
                //将新token放入请求头由前端获取
                response.setHeader("newToken", newToken);
                token = newToken;
            }
            //读取token信息
            Map info = jwtTokenService.getUserInfoFromToken(token);
            String uuid = (String) info.get("uuid");
            List<String> roles = (List) info.get("role");
            if (StringUtils.hasText(uuid)) {
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (roles == null || roles.isEmpty()) {
                    authorities.add(new SimpleGrantedAuthority(RoleEnum.NORMAL.toString()));
                } else {
                    for (String rs : roles) {
                        authorities.add(new SimpleGrantedAuthority(rs));
                    }
                }
                return new UsernamePasswordAuthenticationToken(uuid, token, authorities);
            }
            return null;

        }
        return null;
    }
}
