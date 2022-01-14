/*
 * Copyright (C) 2018 Zhejiang xiaominfo Technology CO.,LTD.
 * All rights reserved.
 * Official Web Site: http://www.xiaominfo.com.
 * Developer Web Site: http://open.xiaominfo.com.
 */

package com.bitop.otcapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2020/11/07 9:26
 * @since:knife4j-spring-boot-fast-demo 1.0
 */
@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfiguration {

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {

        //添加head参数配置start
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("Authorization").description("令牌").modelRef(new ModelRef("string"))
                .parameterType("header").required(false).build();

        ParameterBuilder tokenPar2 = new ParameterBuilder();
        tokenPar2.name("Accept-Language").description("国际化").modelRef(new ModelRef("string"))
                .parameterType("header").defaultValue("zh-CN").required(false).build();

        pars.add(tokenPar.build());
        pars.add(tokenPar2.build());

        Docket docket=new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                //分组名称
                .groupName("2.X版本")
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.bitop.otcapi.fcg.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars);
        return docket;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("bitop项目otc交易所接口文档(王)")
                .description("本 api 提供了bitop app  包括前端 后端 所需接口（有需要请联系：永锋）")
                .termsOfServiceUrl("http://localhost:8080/")
                .contact("xx@qq.com")
                .version("1.0")
                .build();
    }
}
