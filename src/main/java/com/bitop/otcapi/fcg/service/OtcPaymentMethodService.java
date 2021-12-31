package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcPaymentMethod;
import com.bitop.otcapi.fcg.entity.req.PaymentMethodReqDto;
import com.bitop.otcapi.response.Response;

public interface OtcPaymentMethodService extends IService<OtcPaymentMethod> {

    /**
     * @Description: 添加/修改 支付宝收款方式
     * @Param: [alipayReqDto]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/15
     */
    Response alipayPaymentMethod(PaymentMethodReqDto paymentMethodReqDto);
}
