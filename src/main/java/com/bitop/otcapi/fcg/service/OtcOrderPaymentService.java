package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcOrderPayment;

import java.util.List;

public interface OtcOrderPaymentService extends IService<OtcOrderPayment> {

     //存入支付信息
    List<OtcOrderPayment> depositPayment(Integer paymentMethod1, Integer paymentMethod2, Integer paymentMethod3, String userId, String orderNo, String orderMatchNo);
}
