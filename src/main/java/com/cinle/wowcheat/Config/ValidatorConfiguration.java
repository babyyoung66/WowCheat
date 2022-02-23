package com.cinle.wowcheat.Config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;


/**
 * 开启快速失败返回，即只要有一个不通过则立即返回
 * 开启后需要在controller层方法的接收参数前加 @Valid
 * 开启之后，参数在进入controller方法前就捕获错误并返回
 * 需要在全局异常类中捕获 MethodArgumentNotValidException 类，
 * 否则无法获取异常及返回消息
 * @author BabyYoung
 */
@Configuration
public class ValidatorConfiguration {
    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                        .configure()
                        //快速失败返回模式
                        .failFast(true)
                        .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

    /**
     * 开启快速返回
     * 如果参数校验有异常，直接抛异常，不会进入到 controller，使用全局异常拦截进行拦截
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor postProcessor =
                new MethodValidationPostProcessor();
        /*设置validator模式为快速失败返回*/
        postProcessor.setValidator(validator());
        return postProcessor;
    }


}
