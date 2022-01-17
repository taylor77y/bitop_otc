package com.bitop.otcapi.fcg.controller;

import com.bitop.otcapi.aspectj.lang.annotation.AuthToken;
import com.bitop.otcapi.aspectj.lang.annotation.Log;
import com.bitop.otcapi.aspectj.lang.annotation.NoRepeatSubmit;
import com.bitop.otcapi.constant.BusinessType;
import com.bitop.otcapi.constant.LimitType;
import com.bitop.otcapi.constant.OperatorType;
import com.bitop.otcapi.fcg.entity.OtcOrderMatch;
import com.bitop.otcapi.fcg.entity.SearchModel;
import com.bitop.otcapi.fcg.entity.req.PlaceOrderReqDto;
import com.bitop.otcapi.fcg.entity.resp.PaymentDetailsRespDto;
import com.bitop.otcapi.fcg.service.OtcOrderMatchService;
import com.bitop.otcapi.fcg.service.OtcOrderService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "OTC-匹配订单模块")
@RequestMapping("/otc/ordermatch")
public class OtcOrderMatchController {

    @Autowired
    private OtcOrderMatchService orderMatchService;

    @Autowired
    private OtcOrderService orderService;

    @AuthToken
    @ApiOperation(value = "OTC 匹配订单列表")
    @PostMapping("otcOrderList")
    public ResponsePageList<OtcOrderMatch> otcOrderList(@RequestBody SearchModel<OtcOrderMatch> searchModel) {
        return ResponsePageList.success(orderMatchService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }



    @NoRepeatSubmit
    @ApiOperation(value = "用户根据订单号下单购买/出售")
    @PostMapping("placeAnOrder")
    @AuthToken(kyc = true,LIMIT_TYPE = LimitType.BUSINESSLIMIT)
    @Log(title = "下单", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response<PaymentDetailsRespDto> placeAnOrder(@RequestBody PlaceOrderReqDto placeOrderReqDto) {
        return orderService.placeAnOrder(placeOrderReqDto);
    }


    @NoRepeatSubmit
    @ApiOperation(value = "用户 取消订单（两个状态可取消订单  1：接单广告（卖家未接受订单）用户免费取消 " +
            "2：接单广告/普通广告（用户未支付状态） 用户取消次数增加）")
    @ApiImplicitParam(name = "matchOrderNo",value = "matchOrderNo",required = true)
    @PutMapping("cancelOrder/{matchOrderNo}")
    @AuthToken
    @Log(title = "用户 取消订单", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public Response cancelOrder(@PathVariable String matchOrderNo) {
        return orderMatchService.cancelOrder(matchOrderNo);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "买家确认付款")
    @ApiImplicitParam(name = "matchOrderNo",value = "matchOrderNo",required = true)
    @PutMapping("confirmPayment/{matchOrderNo}")
    @AuthToken
    @Log(title = "买家确认付款", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public Response confirmPayment(@PathVariable String matchOrderNo) {
        return orderMatchService.confirmPayment(matchOrderNo);
    }


    @NoRepeatSubmit
    @ApiOperation(value = "卖家放款")
    @ApiImplicitParam(name = "matchOrderNo",value = "matchOrderNo",required = true)
    @PutMapping("sellerPut/{matchOrderNo}")
    @AuthToken
    @Log(title = "卖家放款", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public Response sellerPut(@PathVariable String matchOrderNo) {
        return orderMatchService.sellerPut(matchOrderNo,false);
    }


}
