package com.cinle.wowcheat.Event;

import org.springframework.context.ApplicationEvent;

/**
 * @Author JunLe
 * @Time 2022/4/30 14:05
 */
public class RegisterEvent extends ApplicationEvent {
    public RegisterEvent(Object source) {
        super(source);
    }
}
