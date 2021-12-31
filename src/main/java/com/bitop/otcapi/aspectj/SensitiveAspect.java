/**
 * Create time 2019-06-05 10:34
 * Create by wangkai kiilin@kiilin.com
 * Copyright 2019 kiilin http://www.kiilin.com
 */

package com.bitop.otcapi.aspectj;


import com.bitop.otcapi.aspectj.lang.annotation.Sensitive;
import com.bitop.otcapi.constant.SensitiveConstant;
import com.bitop.otcapi.util.SpringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 注解切面，处理当前请求是否需要脱敏
 *
 * @author wangkai
 * @version V 1.0
 * @since JDK1.8
 */
@Aspect
@Component
public class SensitiveAspect {

    @Pointcut("@within(com.bitop.otcapi.aspectj.lang.annotation.Sensitive) || @annotation(com.bitop.otcapi.aspectj.lang.annotation.Sensitive)")
    public void pointcutByBean() {
    }


    @Before("pointcutByBean()")
    public void before() throws Throwable {

        // 获取请求
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HandlerExecutionChain handler;
        handler = SpringUtils.getBean(HandlerMapping.class).getHandler(request);
        HandlerMethod method = (HandlerMethod) handler.getHandler();
        // 获取controller方法上的注解
        Sensitive sensitive = method.getMethodAnnotation(Sensitive.class);
        if (sensitive == null) {
            sensitive = method.getBeanType().getAnnotation(Sensitive.class);
        }
        // 放入标识
        request.setAttribute(SensitiveConstant.IS_SENSITIVE, sensitive.value());
    }

}
