package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcBankCard;
import com.bitop.otcapi.fcg.entity.req.BankCardReqDto;
import com.bitop.otcapi.fcg.entity.resp.BankCardRespDto;
import com.bitop.otcapi.response.Response;

import java.util.List;

public interface OtcBankCardService extends IService<OtcBankCard> {


    List<BankCardRespDto> userBankCardList(String userId);

    Response addOrUpdateUserBankCard(BankCardReqDto bankCardReqDto);

    Response updateStatusById(BankCardReqDto bankCardReqDto);
}
