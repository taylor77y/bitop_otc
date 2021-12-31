package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcAdvertisingBusiness;
import com.bitop.otcapi.fcg.entity.req.OtcSettingReqDto;
import com.bitop.otcapi.response.Response;

public interface OtcAdvertisingBusinessService extends IService<OtcAdvertisingBusiness> {

    Response otcSetting(OtcSettingReqDto otcSettingReqDto);
}
