/**
 * Create time 2019-05-29 9:48
 * Create by wangkai kiilin@kiilin.com
 * Copyright 2019 kiilin http://www.kiilin.com
 */

package com.bitop.otcapi.aspectj.lang.annotation;

import java.lang.annotation.*;

/**
 * 敏感字段标记注解(使用在controller接口上)
 *
 * @author wanglei
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sensitive {

    /**
     * 是否脱敏
     *
     * @return 是否需要脱敏
     */
    boolean value() default true;

}
