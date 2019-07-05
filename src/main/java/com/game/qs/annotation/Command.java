package com.game.qs.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Created by zun.wei on 2019/6/22.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Command {

    short cmd();


}
