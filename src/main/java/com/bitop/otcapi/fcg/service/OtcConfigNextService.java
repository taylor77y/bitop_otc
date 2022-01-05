package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcConfigNext;
import com.bitop.otcapi.fcg.entity.req.OtcConfigNextReqDto;
import com.bitop.otcapi.response.Response;

public interface OtcConfigNextService extends IService<OtcConfigNext> {

    Response addOrUpdateOtcConfig(OtcConfigNextReqDto otcConfigNextReqDto);
}
