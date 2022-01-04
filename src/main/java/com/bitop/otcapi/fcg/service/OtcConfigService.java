package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcConfig;

public interface OtcConfigService extends IService<OtcConfig> {

    //     * @param type 一键卖币开关  0：普通买卖 上架  1：一键卖币
    void checkOtcStatus( String userId,Integer maxCancelNum);
}
