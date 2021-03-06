package com.bitop.otcapi.fcg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bitop.otcapi.aspectj.lang.annotation.AuthToken;
import com.bitop.otcapi.aspectj.lang.annotation.Log;
import com.bitop.otcapi.aspectj.lang.annotation.NoRepeatSubmit;
import com.bitop.otcapi.constant.BusinessType;
import com.bitop.otcapi.constant.LimitType;
import com.bitop.otcapi.constant.OperatorType;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.fcg.entity.OtcOrder;
import com.bitop.otcapi.fcg.entity.SearchModel;
import com.bitop.otcapi.fcg.entity.req.OtcOrderReqDto;
import com.bitop.otcapi.fcg.entity.req.PlaceOrderReqDto;
import com.bitop.otcapi.fcg.entity.resp.PaymentDetailsRespDto;
import com.bitop.otcapi.fcg.service.OtcOrderService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@Api(tags = "OTC-广告订单模块")
@RequestMapping("/otc/advertisingorder")
public class OtcAdvertisingOrderController {

    @Autowired
    private OtcOrderService otcOrderService;


    @AuthToken
    @ApiOperation(value = "OTC-广告订单列表")
    @PostMapping("otcOrderList")
    public ResponsePageList<OtcOrder> otcOrderList(@RequestBody SearchModel<OtcOrder> searchModel){
        return ResponsePageList.success(otcOrderService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }


    @NoRepeatSubmit
    @ApiOperation(value = "发布广告订单")
    @PostMapping("releaseAdvertisingOrder")
    @AuthToken(kyc = true,LIMIT_TYPE = LimitType.ORDERLIMIT)
    @Log(title = "发布广告订单", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response releaseAdvertisingOrder(@RequestBody @Validated OtcOrderReqDto otcOrderReqDto) {
        otcOrderReqDto.setUserId(ContextHandler.getUserId());
        return otcOrderService.releaseAdvertisingOrder(otcOrderReqDto);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "商户-下架广告订单")
    @ApiImplicitParam(name = "orderNo",value = "orderNo",required = true)
    @PutMapping("offShelfOrder/{orderNo}")
    @AuthToken
    @Log(title = "商户 下架广告订单", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public Response offShelfOrder(@PathVariable String orderNo) {
        return otcOrderService.offShelfOrder(orderNo);
    }



}
