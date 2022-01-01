package com.bitop.otcapi.fcg.controller;

import com.bitop.otcapi.fcg.entity.OtcConfig;
import com.bitop.otcapi.fcg.service.OtcConfigService;
import com.bitop.otcapi.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "OTC-基本配置")
@RequestMapping("/otc/otcConfig")
public class OtcConfigController {

    @Autowired
    private OtcConfigService otcConfigService;

    @ApiOperation(value = "OTC基本配置")
    @PostMapping("otcConfig")
//    @AuthToken
    public Response<OtcConfig> otcConfig() {
        return Response.success(otcConfigService.getById("1"));
    }


//    @NoRepeatSubmit
    @ApiOperation(value = "修改OTC基本配置")
    @PostMapping("updateOtcConfig")
//    @AuthToken
//    @Log(title = "OTC模块", logInfo ="修改OTC基本配置", operatorType = OperatorType.MANAGE)
    public Response updateOtcConfig(@RequestBody OtcConfig ezOtcConfig) {
        otcConfigService.updateById(ezOtcConfig);
        return Response.success();
    }
}
