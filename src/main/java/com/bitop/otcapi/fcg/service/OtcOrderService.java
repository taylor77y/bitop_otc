package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcOrder;
import com.bitop.otcapi.fcg.entity.req.OtcOrderReqDto;
import com.bitop.otcapi.fcg.entity.req.PlaceOrderReqDto;
import com.bitop.otcapi.fcg.entity.resp.PaymentDetailsRespDto;
import com.bitop.otcapi.response.Response;

public interface OtcOrderService extends IService<OtcOrder> {

    Response releaseAdvertisingOrder(OtcOrderReqDto otcOrderReqDto);

    Response offShelfOrder(String orderNo);

    Response<PaymentDetailsRespDto> placeAnOrder(PlaceOrderReqDto placeOrderReqDto);

}
