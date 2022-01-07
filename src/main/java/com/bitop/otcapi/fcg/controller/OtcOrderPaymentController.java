package com.bitop.otcapi.fcg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bitop.otcapi.fcg.entity.OtcOrderPayment;
import com.bitop.otcapi.fcg.service.OtcOrderPaymentService;
import com.bitop.otcapi.response.ResponseList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "OTC-查询用户支付详情模块")
@RequestMapping("/otc/orderpayment")
public class OtcOrderPaymentController {

    @Autowired
    private OtcOrderPaymentService orderPaymentService;

    @ApiOperation(value = "根据匹配订单号查询支付详情")
    @ApiImplicitParam(name = "orderMatchNo",value = "orderMatchNo",required = true)
    @GetMapping("paymentMatchInfo/{orderMatchNo}")
//    @AuthToken
    public ResponseList<OtcOrderPayment> paymentMatchInfo(@PathVariable String orderMatchNo) {
        LambdaQueryWrapper<OtcOrderPayment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OtcOrderPayment::getOrderMatchNo, orderMatchNo);
        return ResponseList.success(orderPaymentService.list(lambdaQueryWrapper));
    }



    //    --------------------------------------------------支付详情
    @ApiOperation(value = "根据上架订单号查询支付详情")
    @ApiImplicitParam(name = "orderNo",value = "orderNo",required = true)
    @GetMapping("paymentOrderInfo/{orderNo}")
//    @AuthToken
    public ResponseList<OtcOrderPayment> paymentOrderInfo(@PathVariable String orderNo) {
        LambdaQueryWrapper<OtcOrderPayment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OtcOrderPayment::getOrderNo, orderNo);
        return ResponseList.success(orderPaymentService.list(lambdaQueryWrapper));
    }
}
