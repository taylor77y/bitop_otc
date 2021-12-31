package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.fcg.entity.OtcBankCard;
import com.bitop.otcapi.fcg.entity.OtcOrderAppeal;
import com.bitop.otcapi.fcg.mapper.OtcBankCardMapper;
import com.bitop.otcapi.fcg.mapper.OtcOrderAppealMapper;
import com.bitop.otcapi.fcg.service.OtcBankCardService;
import com.bitop.otcapi.fcg.service.OtcOrderAppealService;
import org.springframework.stereotype.Service;

@Service
public class OtcOrderAppealServiceImpl extends ServiceImpl<OtcOrderAppealMapper, OtcOrderAppeal> implements OtcOrderAppealService {
}
