package com.bitop.otcapi.fcg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.fcg.entity.OtcPaymentMethod;
import com.bitop.otcapi.fcg.entity.req.PaymentMethodReqDto;
import com.bitop.otcapi.fcg.service.OtcPaymentMethodService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponseList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Slf4j
@RestController
@Api(tags = "OTC-用户收款方式模块")
@RequestMapping("/otc/paymentmethod")
public class OtcPaymentMethodController {

    @Autowired
    private OtcPaymentMethodService otcPaymentMethodService;

//    @AuthToken(kyc = true)
    @ApiOperation(value = "收款方式 列表")
    @GetMapping("paymentInfoList")
    public ResponseList<OtcPaymentMethod> paymentMethodList() {
        //判断是否实名认证
//        LambdaQueryWrapper<EzUserKyc> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(EzUserKyc::getUserId, ContextHandler.getUserId());
//        EzUserKyc one = kycService.getOne(lambdaQueryWrapper);
//        if (one==null){
//            return ResponseList.error(MessageUtils.message("请先完成实名认证"), Collections.EMPTY_LIST);
//        }

        LambdaQueryWrapper<OtcPaymentMethod> alipayQueryWrapper = new LambdaQueryWrapper<>();
        alipayQueryWrapper.eq(OtcPaymentMethod::getUserId, "2147483647");//ContextHandler.getUserId()
        return ResponseList.success(otcPaymentMethodService.list(alipayQueryWrapper));
    }

//    @NoRepeatSubmit
//    @AuthToken(kyc = true)
    @ApiOperation(value = "添加/修改收款方式")
    @PostMapping("updateOrAddPaymentInfo")
//    @Log(title = "添加收款方式", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response updateOrAddPaymentInfo(@RequestBody @Validated PaymentMethodReqDto paymentMethodReqDto) {
        return otcPaymentMethodService.alipayPaymentMethod(paymentMethodReqDto);
    }

//    @NoRepeatSubmit
//    @AuthToken
    @ApiOperation(value = "删除收款方式")
    @ApiImplicitParam(name = "id",value = "id",required = true)
    @PostMapping("deletePaymentInfo/{id}")
//    @Log(title = "删除收款方式", businessType = BusinessType.DELETE, operatorType = OperatorType.MOBILE)
    public Response deletePaymentInfo(@PathVariable String id) {
        otcPaymentMethodService.removeById(id);
        return Response.success();
    }
}
