package com.bitop.otcapi.fcg.controller;

import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.fcg.entity.OtcOrder;
import com.bitop.otcapi.fcg.entity.SearchModel;
import com.bitop.otcapi.fcg.entity.req.OtcOrderReqDto;
import com.bitop.otcapi.fcg.service.OtcOrderService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponsePageList;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class OtcAdvertisingOrderController {

    @Autowired
    private OtcOrderService otcOrderService;


//    @AuthToken
    @ApiOperation(value = "OTC-广告订单列表")
    @PostMapping("otcOrderList")
    public ResponsePageList<OtcOrder> otcOrderList(@RequestBody SearchModel<OtcOrder> searchModel){
        return ResponsePageList.success(otcOrderService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }


//    @NoRepeatSubmit
    @ApiOperation(value = "发布广告订单")
    @PostMapping("releaseAdvertisingOrder")
//    @AuthToken(kyc = true,LIMIT_TYPE = LimitType.ORDERLIMIT)
//    @Log(title = "发布广告订单", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response releaseAdvertisingOrder(@RequestBody @Validated OtcOrderReqDto otcOrderReqDto) {
        otcOrderReqDto.setUserId(ContextHandler.getUserId());
        return otcOrderService.releaseAdvertisingOrder(otcOrderReqDto);
    }
}
