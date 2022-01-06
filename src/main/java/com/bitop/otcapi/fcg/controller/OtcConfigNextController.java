package com.bitop.otcapi.fcg.controller;

import com.bitop.otcapi.fcg.entity.OtcConfigNext;
import com.bitop.otcapi.fcg.entity.req.OtcConfigNextReqDto;
import com.bitop.otcapi.fcg.service.OtcConfigNextService;
import com.bitop.otcapi.fcg.service.OtcConfigService;
import com.bitop.otcapi.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Api(tags = "OTC-次级菜单-OTC配置")
@RequestMapping("/otc/otcConfig")
public class OtcConfigNextController {


    @Autowired
    private OtcConfigNextService otcConfigNextService;


    @ApiOperation(value = "新增 次级菜单-OTC 配置")//sellUserId   buyUserId
//    @AuthToken
    @PostMapping("addOrUpdateOtcConfig")
//    @Log(title = "添加/修改 网络账号信息状态", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response<OtcConfigNext> addOrUpdateOtcConfig(@RequestBody @Valid OtcConfigNextReqDto otcConfigNextReqDto){
        return otcConfigNextService.addOrUpdateOtcConfig(otcConfigNextReqDto);
    }
}
