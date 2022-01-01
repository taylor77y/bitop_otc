package com.bitop.otcapi.fcg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bitop.otcapi.fcg.entity.OtcCountryConfig;
import com.bitop.otcapi.fcg.entity.SearchModel;
import com.bitop.otcapi.fcg.entity.req.CountryReqDto;
import com.bitop.otcapi.fcg.service.OtcCountryConfigService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponsePageList;
import com.bitop.otcapi.util.BeanUtils;
import com.bitop.otcapi.util.MessageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "OTC-国家编号管理模块")
@RequestMapping("/otc/countryConfig")
public class OtcCountryConfigController {

    @Autowired
    private OtcCountryConfigService countryConfigService;

    @ApiOperation(value = "国家编号配置列表")
    @PostMapping("/countryConfigs")
//    @AuthToken
    public ResponsePageList<OtcCountryConfig> countryConfigs(@RequestBody SearchModel<OtcCountryConfig> searchModel){
        return ResponsePageList.success(countryConfigService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @ApiOperation(value = "添加国家编号配置")
    @PostMapping("/addCountryConfigs")
//    @AuthToken
//    @Log(title = "配置模块", logInfo ="添加国家编号配置", operatorType = OperatorType.MANAGE)
    public Response addCountryConfigs(@RequestBody @Validated CountryReqDto countryReqDto){
        //判断配置是否存在
        LambdaQueryWrapper<OtcCountryConfig> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(OtcCountryConfig::getCountryName,countryReqDto.getCountryName())
                .eq(OtcCountryConfig::getCountryNameEn,countryReqDto.getCountryNameEn())
                .eq(OtcCountryConfig::getCountryCode,countryReqDto.getCountryCode())
                .eq(OtcCountryConfig::getCountryTelCode,countryReqDto.getCountryTelCode());

        OtcCountryConfig countryConfig = countryConfigService.getOne(queryWrapper,true);
        if (countryConfig!=null){
            return Response.error(MessageUtils.message("内容重复"));
        }
        OtcCountryConfig ezCountryConfig = new OtcCountryConfig();
//        BeanUtils.copyBeanProp(ezCountryConfig,countryReqDto);
        BeanUtils.copyProperties(countryReqDto, ezCountryConfig);
        countryConfigService.save(ezCountryConfig);
        return Response.success();
    }

    @ApiOperation(value = "批量删除国家编号配置")
    @DeleteMapping("/removeCountryConfigs")
//    @AuthToken
//    @Log(title = "配置模块", logInfo ="批量删除国家编号配置", operatorType = OperatorType.MANAGE)
    public Response removeCountryConfigs(@RequestBody List<String> idList){
        countryConfigService.removeByIds(idList);
        return Response.success();
    }

}
