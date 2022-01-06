package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcCoinConfig;
import com.bitop.otcapi.fcg.entity.resp.CoinConfigRespDto;

import java.util.List;

public interface OtcCoinConfigService extends IService<OtcCoinConfig> {

    /**
     * 查询所有 coin 挂单配置信息
     * @param transactionType
     * @return
     */
    List<CoinConfigRespDto> getAllOTCTransctionConfig(String transactionType);

}
