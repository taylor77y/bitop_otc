package com.bitop.otcapi.aspectj.lang.annotation;

import com.bitop.otcapi.constant.LimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthToken {
    //是否验证用户高级认证状态
    boolean advertisingStatus() default false;
    //是否验证用户kyc状态
    boolean kyc() default false;
    //是否验证用户状态
    boolean status() default true;
    /**
     * 验证禁用类型
     */
    public LimitType LIMIT_TYPE() default LimitType.NOLIMIT;
    /**
     * 验证管理员code码
     */
    public String CODE() default "-1";
}