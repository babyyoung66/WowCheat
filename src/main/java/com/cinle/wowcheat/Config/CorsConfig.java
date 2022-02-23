package com.cinle.wowcheat.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author JunLe
 * @Time 2022/2/22 17:18
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * 跨域访问配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://127.0.0.1")  //springboot2.6 之后强制指定，不给使用*
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .maxAge(3600)
               /* .exposedHeaders("Access-Control-Allow-Headers",
                "Access-Control-Allow-Methods",
                "Access-Control-Allow-Origin",
                "Access-Control-Max-Age",
                "X-Frame-Options")*/

                ;
    }
}
