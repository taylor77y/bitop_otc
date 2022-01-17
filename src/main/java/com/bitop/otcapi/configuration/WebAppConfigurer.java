package com.bitop.otcapi.configuration;

import com.bitop.otcapi.handler.AuthenticationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 可添加多个
        registry.addInterceptor(getAuthenticationInterceptor())
                .addPathPatterns("/**")
//                .excludePathPatterns(
//                        "/admin/acl/validate/login")
        ;
    }

    @Bean
    public AuthenticationInterceptor getAuthenticationInterceptor() {
        return new AuthenticationInterceptor();
    }
}
