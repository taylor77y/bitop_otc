package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.fcg.entity.OtcCoinConfig;
import com.bitop.otcapi.fcg.entity.resp.CoinConfigRespDto;
import com.bitop.otcapi.fcg.mapper.OtcCoinConfigMapper;
import com.bitop.otcapi.fcg.mapper.OtcConfigNextMapper;
import com.bitop.otcapi.fcg.service.OtcCoinConfigService;
import com.bitop.otcapi.util.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OtcCoinConfigServiceImpl extends ServiceImpl<OtcCoinConfigMapper, OtcCoinConfig> implements OtcCoinConfigService {

    /**
     * 查询所有 coin 挂单配置信息
     *
     * @param
     * @return
     */
    @Override
    public List<CoinConfigRespDto> getAllOTCTransctionConfig(String transactionType) {
        LambdaQueryWrapper<OtcCoinConfig> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OtcCoinConfig::getTransactionType, transactionType);
        List<OtcCoinConfig> lists = baseMapper.selectList(lambdaQueryWrapper);
        return BeanUtils.copyListProperties(lists, CoinConfigRespDto::new);
    }
}
