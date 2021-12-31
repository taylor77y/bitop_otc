package com.bitop.otcapi.fcg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitop.otcapi.fcg.entity.OtcBankCard;
import com.bitop.otcapi.fcg.entity.OtcInternetAccount;

public interface OtcBankCardMapper extends BaseMapper<OtcBankCard> {

    int save(OtcBankCard otcBankCard);

    int updateById(OtcBankCard otcBankCard);
}
