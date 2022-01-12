package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.fcg.entity.OtcCountryConfig;
import com.bitop.otcapi.fcg.entity.OtcOrderIndex;
import com.bitop.otcapi.fcg.mapper.OtcOrderIndexMapper;
import com.bitop.otcapi.fcg.service.OtcCountryConfigService;
import com.bitop.otcapi.fcg.service.OtcOrderIndexService;
import com.bitop.otcapi.util.OrderNoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OtcOrderIndexServiceImpl extends ServiceImpl<OtcOrderIndexMapper, OtcOrderIndex> implements OtcOrderIndexService {

    @Autowired
    private OtcCountryConfigService countryConfigService;

    @Autowired
    private OtcOrderIndexMapper orderIndexMapper;

    @Override
    public String getOrderNoByCountryCode(String countryCode,String id) {
        OtcOrderIndex index = orderIndexMapper.getOneByName(id);
        Integer currentValue = index.getCurrentValue();
        String orderNo = OrderNoUtils.getOrderNo();
        Integer other = index.getOther();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(countryCode);
        stringBuilder.append(other);
        stringBuilder.append(orderNo);

        //订单拼串
        String sequenceStr = String.valueOf(currentValue);
        for (int i = 0; i <6 - sequenceStr.length(); i++) {
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);

        int nextSequence = index.getCurrentValue()+index.getStep() ;
        if (nextSequence >= 1000000) {
            index.setCurrentValue(0);
            baseMapper.updateById(index);
        }else {
            index.setCurrentValue(nextSequence);
            baseMapper.updateById(index);
        }
        return String.valueOf(stringBuilder);
    }

    /**
     * 根据国家货币获取订单号
     *
     * @param currencyCode
     * @param id
     */
    @Override
    public String getOrderNoByCurrencyCode(String currencyCode, String id) {
        LambdaQueryWrapper<OtcCountryConfig> configLambdaQueryWrapper = new LambdaQueryWrapper<>();
        configLambdaQueryWrapper.eq(OtcCountryConfig::getCurrencyCode, currencyCode);
        OtcCountryConfig one = countryConfigService.getOne(configLambdaQueryWrapper);
        String countryCode = one.getCountryCode();//国家编号
        return getOrderNoByCountryCode(countryCode,id);
    }
}
