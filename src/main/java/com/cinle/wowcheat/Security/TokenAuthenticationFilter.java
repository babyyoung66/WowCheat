package com.cinle.wowcheat.Security;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Enum.RoleEnum;
import com.cinle.wowcheat.Service.JwtTokenService;
import com.cinle.wowcheat.Service.RoleServices;
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
import java.nio.file.AccessDeniedException;
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
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //System.out.println("authentication = " + authentication);

        UsernamePasswordAuthenticationToken authenticationToken = null;
        try {
            authenticationToken = getAuthentication(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            AjaxResponse ajaxResponse = new AjaxResponse();
            ajaxResponse.error().setMessage("Token处理失败！" + e.getClass());
            PrintWriter out = response.getWriter();
            out.println(JSON.toJSONString(ajaxResponse));
            out.flush();
            out.close();
        }
        // System.out.println("authenticationToken = " + authenticationToken);
        if (authenticationToken != null) {
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response);
        } else {
            return;
            /** 为空时
             * 交给security的过滤链处理，不然就自己校验URL
             * 由 FilterSecurityInterceptor 最后决定是否通过
             * 该过滤器是SpringSecurity最终决定是否放行的过滤器
             * */
            /*AjaxResponse ajaxResponse = new AjaxResponse();
            response.setStatus(403);
            ajaxResponse.error().setMessage("无权限操作，请联系管理员！").setCode(403);
            PrintWriter out = response.getWriter();
            out.println(JSON.toJSONString(ajaxResponse));
            out.flush();
            out.close();
            return;*/
        }

        //super.doFilterInternal(request, response, chain);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AjaxResponse ajaxResponse = new AjaxResponse();
        response.setContentType("application/json;charset=UTF-8");
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            //为空则尝试从parameter获取
            token = request.getParameter("token");
        }

        if (StringUtils.hasText(token)) {
            boolean isExpires = jwtTokenService.isTokenExpires(token);
            if (isExpires){
                response.setStatus(401);
                ajaxResponse.error().setMessage("Token已过期，请重新登录！").setCode(403);
                PrintWriter out = response.getWriter();
                out.println(JSON.toJSONString(ajaxResponse));
                out.flush();
                out.close();
                return null;
            }

            //校验合法性
            Map info = jwtTokenService.getUserInfoFromToken(token);
            String uuid = (String) info.get("uuid");
            System.out.println("uuid = " + uuid);
            boolean isOnRedis = jwtTokenService.CheckTokenByRedis(uuid,token);
            if (!isOnRedis || info.isEmpty()) {
                response.setStatus(401);
                ajaxResponse.error().setMessage("Token失效，请重新登录！").setCode(403);
                PrintWriter out = response.getWriter();
                out.println(JSON.toJSONString(ajaxResponse));
                out.flush();
                out.close();
                return null;
            }

            //是否需要刷新token
            String newToken = jwtTokenService.isNeedFlushToken(token);
            if (StringUtils.hasText(newToken)) {
                //将新token放入请求头由前端获取
                response.setHeader("newToken", newToken);
                token = newToken;
            }

            List<String> roles = (List) info.get("role");
            if (StringUtils.hasText(uuid)) {
                //List<Role> r = roleServices.selectByUseruid(uuid); //关闭session后每次都会查询MySQL，已弃用
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (roles == null || roles.isEmpty()) {
                    authorities.add(new SimpleGrantedAuthority(RoleEnum.NORMAL.getName()));
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
