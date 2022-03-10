package com.cinle.wowcheat.Config;

import com.cinle.wowcheat.Constants.FileConst;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author JunLe
 * @Time 2022/3/1 22:35
 */
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    private final static String BASE_PATH = System.getProperty("user.home");
    /**
     * 设置静态资源映射路径
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + FileConst.LOCAL_PATH);
    }
}
