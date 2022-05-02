package com.cinle.wowcheat.Security;

import com.cinle.wowcheat.Constants.FileConst;
import com.cinle.wowcheat.Service.RoleServices;
import com.cinle.wowcheat.WebSocket.SocketConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author BabyYoung
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserLoginService userLoginService;
    @Autowired
    private MyAccessDeniedHandler accessDeniedHandler;
    @Autowired
    RoleServices roleServices;
    @Autowired
    JwtTokenService jwtTokenService;
    @Autowired
    LoginSuccessHandler loginSuccessHandler;
    @Autowired
    CustomerLogoutSuccessHandler logoutSuccessHandler;

    //权限认证
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/register/**", "/auth/**","/user/getTestUser").permitAll()
                //放行swagger
                .antMatchers("/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/v2/**", "/api/**", "/swagger-ui/**").permitAll()
                //放行websocket
                .antMatchers(SocketConstants.CONNECT_PATH + "/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().authenticated()
                // .access("@checkRoles.hasPermission(request,authentication)")
                .and()
                .addFilter(new TokenAuthenticationFilter(authenticationManager()).setService(jwtTokenService)).httpBasic()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomerAuthenticationEntryPoint()) //未登录处理
                .accessDeniedHandler(accessDeniedHandler) //未授权、权限不对应
                .and()
                .cors(Customizer.withDefaults())//Customizer.withDefaults(),前后端分离JSON登录必须加该内容
                .csrf().disable()
        ;

        //关闭stomp的frame验证
        http.headers().frameOptions().disable();
        http.addFilterAt(CustomerUsernamePasswordFilter(), UsernamePasswordAuthenticationFilter.class); /*自定义获取JSON账号密码方法*/

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER); //开启或关闭session，使用token时可以关闭



        /*退出相关*/
        http.logout()
                .logoutUrl("/auth/logout")
                .logoutSuccessHandler(logoutSuccessHandler)
        ;

    }



//
//    @Bean
//    HttpSessionEventPublisher httpSessionEventPublisher() {
//        return new HttpSessionEventPublisher();
//    }
//
//    @Bean
//    public SessionRegistry sessionRegistry() {
//        return new SessionRegistryImpl();
//    }
//

    //身份认证
    //spring5.0+需要密码加密才可以认证，所有数据库加入数据时需对密码加密
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userLoginService)//加入自定义的认证方法
                .passwordEncoder(new BCryptPasswordEncoder())
        ;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //放行静态资源
        web.ignoring().antMatchers( FileConst.ACCESS_PATH + "**")
                       .antMatchers(SocketConstants.CONNECT_PATH + "/**") ;
    }

    /**
     * 重写获取登录信息的方法
     * 使之支持JSON登录
     */
    @Bean
    public CustomerUsernamePasswordFilter CustomerUsernamePasswordFilter() throws Exception {
        CustomerUsernamePasswordFilter filter = new CustomerUsernamePasswordFilter();
        //设置处理成功处理器
//        filter.setAuthenticationSuccessHandler();
        //设置过滤器拦截路径，即开启JSON登录的路径
        filter.setFilterProcessesUrl("/auth/login");
        //设置自定义成功返回消息
        filter.setAuthenticationSuccessHandler(loginSuccessHandler);
        //设置自定义验证失败消息
        filter.setAuthenticationFailureHandler(new LoginFailHandler());
        //这里设置自带的AuthenticationManager，否则要自己写一个
        filter.setAuthenticationManager(super.authenticationManager());
        //加入自定义获取remember的方法
        //filter.setRememberMeServices(CustomerRememberMeServices());
        return filter;
    }


//    /**
//     * @return 注入自定义的判断Rememberme的方法
//     */
//    @Bean
//    public CustomerRememberMeServices CustomerRememberMeServices() {
//        CustomerRememberMeServices rememberMeServices = new CustomerRememberMeServices("INTERNAL_SECRET_KEY", userLoginService, persistentTokenRepository());
//        rememberMeServices.setParameter("rememberMe"); // 修改默认参数remember-me为rememberMe和前端请求中的key要一致
//        rememberMeServices.setTokenValiditySeconds(3600 * 24 * 7); //token有效期7天
//        rememberMeServices.setCookieName("WowRemember");
//        return rememberMeServices;
//    }
//
//
//    @Bean
//    public RememberMeAuthenticationFilter rememberMeAuthenticationFilter() throws Exception {
//        //重用WebSecurityConfigurerAdapter配置的AuthenticationManager，不然要自己组装AuthenticationManager
//        RememberMeAuthenticationFilter filter = new RememberMeAuthenticationFilter(super.authenticationManager(), CustomerRememberMeServices());
//        return filter;
//    }


//    /**
//     * 使用Security提供的默认方法将token持久化到数据库，在数据库创建persistent_logins表，
//     * 表结构在
//     * @see JdbcTokenRepositoryImpl
//     * 类中的静态常量 CREATE_TABLE_SQL，即
//     * create table persistent_logins (username varchar(64) not null,
//     * series varchar(64) primary key,token varchar(64) not null,
//     * last_used timestamp not null)
//     **/
//    @Resource
//    private DataSource dataSource;
//
//    @Bean
//    public PersistentTokenRepository persistentTokenRepository() {
//
//        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
//        tokenRepository.setDataSource(dataSource);
//        return tokenRepository;
//    }

}
