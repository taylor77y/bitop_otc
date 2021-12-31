package com.bitop.otcapi.aspectj.lang.annotation;

import java.lang.annotation.*;

/**
 * @author fueen
 * @date 2020/7/4
 * 自定义防重复提交注解
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoRepeatSubmit {
    /**
     * 默认时间  默认1秒钟
     * @return
     */
    int lockTime() default 1500;
}

