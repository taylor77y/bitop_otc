package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.fcg.entity.OtcCountryConfig;
import com.bitop.otcapi.fcg.entity.OtcInternetAccount;
import com.bitop.otcapi.fcg.mapper.OtcCountryConfigMapper;
import com.bitop.otcapi.fcg.mapper.OtcInternetAccountMapper;
import com.bitop.otcapi.fcg.service.OtcCountryConfigService;
import com.bitop.otcapi.fcg.service.OtcInternetAccountService;
import org.springframework.stereotype.Service;

@Service
public class OtcCountryConfigServiceImpl extends ServiceImpl<OtcCountryConfigMapper, OtcCountryConfig> implements OtcCountryConfigService {
}
