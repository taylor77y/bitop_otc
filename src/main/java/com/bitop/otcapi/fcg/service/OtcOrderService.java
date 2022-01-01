package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcOrder;
import com.bitop.otcapi.fcg.entity.req.OtcOrderReqDto;
import com.bitop.otcapi.response.Response;

public interface OtcOrderService extends IService<OtcOrder> {

    Response releaseAdvertisingOrder(OtcOrderReqDto otcOrderReqDto);

}
