package com.cinle.wowcheat.Config;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author JunLe
 * @Time 2022/2/22 14:58
 * 默认访问地址 ：swagger-ui.html 或 swagger-ui/index.html
 * 配置文件 swagger.enable 值为true时启用
 */
@ConditionalOnProperty(name = "swagger.enable",havingValue = "true")
@Configuration
@EnableOpenApi
public class SwaggerConfig {


    //构建 api文档的详细信息
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("API接口文档")
                .licenseUrl("www.baidu.com")
                //创建人
                .contact(new Contact("@Author Junle", "www.xxx.com", "1515251178@qq.com"))
                //版本号
                .version("1.0")
                //描述
                .description("API 描述")
                .build();
    }

    /*不做分组，分组时设置多个Bean选择对应Controller即可*/
    @Bean
    public Docket createRestApiForUer(){

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo()).groupName("Controller")
                .select()
                //为controller包路径
                //.apis(RequestHandlerSelectors.basePackage("com.xxx.xx.Controller"))
                //生成使用了Api注解的类的文档
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                //包下的所有路径，不做分组
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    //分组
   /* @Bean
    public Docket createRestApiForAddress(){
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo()).groupName("Role接口组")
                .select()
                //为controller包路径
                .apis(RequestHandlerSelectors.basePackage("com.cinle.wowcheat.Controller.RoleController"))
                .build();
        return docket;
    }
*/

}
