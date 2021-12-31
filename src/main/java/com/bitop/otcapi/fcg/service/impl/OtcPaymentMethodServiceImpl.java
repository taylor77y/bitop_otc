package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.constant.PaymentMethod;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.fcg.entity.OtcPaymentMethod;
import com.bitop.otcapi.fcg.entity.req.PaymentMethodReqDto;
import com.bitop.otcapi.fcg.mapper.OtcPaymentMethodMapper;
import com.bitop.otcapi.fcg.service.OtcPaymentMethodService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.util.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OtcPaymentMethodServiceImpl extends ServiceImpl<OtcPaymentMethodMapper, OtcPaymentMethod> implements OtcPaymentMethodService {

    /**
     * @param paymentMethodReqDto
     * @Description: 添加/修改 二维码类型  收款方式
     * @return: void
     * @Author: taylor
     * @Date: 2021/12/15
     */
    @Override
    public Response alipayPaymentMethod(PaymentMethodReqDto paymentMethodReqDto) {
        if (paymentMethodReqDto.getPaymentMethodId() == PaymentMethod.BANK.getCode()) {
            if (StringUtils.isEmpty(paymentMethodReqDto.getBankName())) {
                return Response.error("请输入银行名称");
            }
        } else {
            if (StringUtils.isEmpty(paymentMethodReqDto.getPaymentQrCode())) {
                return Response.error("请先上传支付二维码");
            }
        }
        String userId = ContextHandler.getUserId();
        String id = paymentMethodReqDto.getId();
        OtcPaymentMethod paymentInfo = new OtcPaymentMethod();//copyBeanProp(paymentInfo, paymentMethodReqDto);
        BeanUtils.copyProperties(paymentMethodReqDto, paymentInfo);
        paymentInfo.setUserId(userId);
        if (StringUtils.isEmpty(id)) {//添加
            //查看是否纯在此支付方式
            LambdaQueryWrapper<OtcPaymentMethod> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OtcPaymentMethod::getUserId, userId);
            lambdaQueryWrapper.eq(OtcPaymentMethod::getPaymentMethodId, paymentMethodReqDto.getPaymentMethodId());
            Integer integer = baseMapper.selectCount(lambdaQueryWrapper);
            if (integer != 0) {
                return Response.error("每种支付方式只能上传一种");
            }
            baseMapper.insert(paymentInfo);
        } else {//修改
            baseMapper.updateById(paymentInfo);
        }
        return Response.success();
    }
}