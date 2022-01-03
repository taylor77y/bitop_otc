package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.fcg.entity.CoinAccount;
import com.bitop.otcapi.fcg.entity.CoinRecord;
import com.bitop.otcapi.fcg.mapper.CoinAccountMapper;
import com.bitop.otcapi.fcg.mapper.CoinRecordMapper;
import com.bitop.otcapi.fcg.service.CoinAccountService;
import com.bitop.otcapi.fcg.service.CoinRecordService;
import org.springframework.stereotype.Service;

@Service
public class CoinRecordServiceImpl extends ServiceImpl<CoinRecordMapper, CoinRecord> implements CoinRecordService {
}
