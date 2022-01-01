package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcOrderIndex;

public interface OtcOrderIndexService extends IService<OtcOrderIndex> {

    /**
     * 根据国家代码获取订单号
     * @param countryCode
     * @param id
     * @return
     */
    String getOrderNoByCountryCode(String countryCode,String id);

    /**
     * 根据国家货币获取订单号
     */
    String getOrderNoByCurrencyCode(String currencyCode,String id);
}
