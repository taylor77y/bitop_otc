package com.bitop.otcapi.fcg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bitop.otcapi.fcg.entity.OtcCountryConfig;
import com.bitop.otcapi.fcg.service.OtcCountryConfigService;
import com.bitop.otcapi.response.ResponseList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@Api(tags = "OTC-国家货币代码")
@RequestMapping("/otc/currencyconfig")
public class OtcCurrencyConfigController {

    @Autowired
    private OtcCountryConfigService countryConfigService;

    @ApiOperation(value = "国家货币列表")
    @GetMapping("currencyCodeList")
    public ResponseList<String> currencyCodeList(){
        LambdaQueryWrapper<OtcCountryConfig> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(OtcCountryConfig::getCurrencyCode);
        lambdaQueryWrapper.isNotNull(OtcCountryConfig::getCurrencyCode);
        lambdaQueryWrapper.orderByAsc(OtcCountryConfig::getSort);
        return ResponseList.success(countryConfigService.listObjs(lambdaQueryWrapper).stream().map(o ->  (String) o).collect(Collectors.toList()));
    }
}
