package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcBankCard;
import com.bitop.otcapi.fcg.entity.OtcOrderAppeal;
import com.bitop.otcapi.fcg.entity.req.AppealReqDto;
import com.bitop.otcapi.response.Response;

public interface OtcOrderAppealService extends IService<OtcOrderAppeal> {

    /**
     * 订单申诉
     * @param appealReqDto
     * @return
     */
    Response appeal(AppealReqDto appealReqDto);

    /**
     * 取消申诉
     * @param id
     * @return
     */
    Response cancelAppeal(String id);
}
