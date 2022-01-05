package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcAdvertisingBusiness;
import com.bitop.otcapi.fcg.entity.req.OtcSettingReqDto;
import com.bitop.otcapi.response.Response;

import java.util.Date;

public interface OtcAdvertisingBusinessService extends IService<OtcAdvertisingBusiness> {

    //完善otc交易信息
    Response otcSetting(OtcSettingReqDto otcSettingReqDto);

    void updateCount(String sellUserId, String buyUserId, Date payTime, Date finishTime, boolean isAdmin, String status);
}
