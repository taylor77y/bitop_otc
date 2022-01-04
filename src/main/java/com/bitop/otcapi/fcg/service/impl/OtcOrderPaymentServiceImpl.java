package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.exception.BaseException;
import com.bitop.otcapi.fcg.entity.OtcOrderPayment;
import com.bitop.otcapi.fcg.entity.OtcPaymentMethod;
import com.bitop.otcapi.fcg.mapper.OtcOrderPaymentMapper;
import com.bitop.otcapi.fcg.service.OtcCountryConfigService;
import com.bitop.otcapi.fcg.service.OtcOrderPaymentService;
import com.bitop.otcapi.fcg.service.OtcPaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class OtcOrderPaymentServiceImpl extends ServiceImpl<OtcOrderPaymentMapper, OtcOrderPayment> implements OtcOrderPaymentService {


    @Autowired
    private OtcPaymentMethodService paymentMethodService;

    /**
     * 存入支付信息
     *
     * @param paymentMethod1
     * @param paymentMethod2
     * @param paymentMethod3
     */
    @Override
    public List<OtcOrderPayment> depositPayment(Integer paymentMethod1, Integer paymentMethod2, Integer paymentMethod3, String userId, String orderNo, String orderMatchNo) {
        LambdaQueryWrapper<OtcPaymentMethod> queryWrapper1 = Wrappers.<OtcPaymentMethod>lambdaQuery().
                eq(OtcPaymentMethod::getUserId, userId).and(wq->wq.eq(OtcPaymentMethod::getPaymentMethodId, paymentMethod1)
                .or().eq(OtcPaymentMethod::getPaymentMethodId, paymentMethod2)
                .or().eq(OtcPaymentMethod::getPaymentMethodId, paymentMethod3));
        List<OtcPaymentMethod> list = paymentMethodService.list(queryWrapper1);
        if (CollectionUtils.isEmpty(list)){
            throw new BaseException(null, "801","未匹配到支付方式" ,null );
        }
        List<OtcOrderPayment> list1 = new ArrayList<OtcOrderPayment>();
        list.forEach(e -> {
            OtcOrderPayment ezOtcOrderPayment = new OtcOrderPayment();
            ezOtcOrderPayment.setPaymentQrCode(e.getPaymentQrCode());
            ezOtcOrderPayment.setPaymentMethodId(e.getPaymentMethodId());
            ezOtcOrderPayment.setAccountNumber(e.getAccountNumber());
            ezOtcOrderPayment.setBankName(e.getBankName());
            ezOtcOrderPayment.setRealName(e.getRealName());
            if (StringUtils.hasText(orderNo)){
                ezOtcOrderPayment.setType("0");
                ezOtcOrderPayment.setOrderNo(orderNo);
            }else {
                ezOtcOrderPayment.setType("1");
                ezOtcOrderPayment.setOrderMatchNo(orderMatchNo);
            }
            list1.add(ezOtcOrderPayment);
        });
        this.saveBatch(list1);
        if (StringUtils.hasText(orderMatchNo)){
            return list1;
        }else {
            return null;
        }
    }
}
