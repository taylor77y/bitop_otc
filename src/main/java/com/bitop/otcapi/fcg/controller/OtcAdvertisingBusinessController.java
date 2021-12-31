package com.bitop.otcapi.fcg.controller;

import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.fcg.entity.OtcAdvertisingBusiness;
import com.bitop.otcapi.fcg.entity.SearchModel;
import com.bitop.otcapi.fcg.entity.req.BusinessReqDto;
import com.bitop.otcapi.fcg.service.OtcAdvertisingBusinessService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponsePageList;
import com.bitop.otcapi.util.BeanUtils;
import com.bitop.otcapi.util.EncoderUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "OTC广告商户模块")
@RequestMapping("/otc/ezAdvertisingBusiness")
public class OtcAdvertisingBusinessController {

    @Autowired
    private OtcAdvertisingBusinessService advertisingBusinessService;

    @ApiOperation(value = "OTC广告商户列表")
    @PostMapping("advertisingBusinessList")
//    @AuthToken
    public ResponsePageList<OtcAdvertisingBusiness> advertisingBusinessList(@RequestBody SearchModel<OtcAdvertisingBusiness> searchModel) {
        return ResponsePageList.success(advertisingBusinessService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @ApiOperation(value = "修改OTC广告商户信息")
    @PostMapping("updateAdvertisingBusiness")
//    @AuthToken
//    @Log(title = "OTC模块", logInfo ="修改商户信息", operatorType = OperatorType.MANAGE)
    public Response updateAdvertisingBusiness(@RequestBody BusinessReqDto businessReqDto){
        OtcAdvertisingBusiness advertisingBusiness = new OtcAdvertisingBusiness();
        BeanUtils.copyProperties(businessReqDto, advertisingBusiness);
        if (StringUtils.hasLength(businessReqDto.getSecurityPassword())){
            advertisingBusiness.setSecurityPassword(EncoderUtil.encode(businessReqDto.getSecurityPassword()));
        }
        advertisingBusiness.setUpdateBy(ContextHandler.getUserName());
        advertisingBusinessService.updateById(advertisingBusiness);
        return Response.success();

    }
}
