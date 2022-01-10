package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.fcg.entity.OtcUserLimit;
import com.bitop.otcapi.fcg.mapper.OtcUserLimitMapper;
import com.bitop.otcapi.fcg.service.OtcUserLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OtcUserLimitServiceImpl extends ServiceImpl<OtcUserLimitMapper, OtcUserLimit> implements OtcUserLimitService {
}
