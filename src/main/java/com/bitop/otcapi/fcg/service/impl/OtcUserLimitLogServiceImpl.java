package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.fcg.entity.OtcUserLimitLog;
import com.bitop.otcapi.fcg.mapper.OtcUserLimitLogMapper;
import com.bitop.otcapi.fcg.service.OtcUserLimitLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OtcUserLimitLogServiceImpl extends ServiceImpl<OtcUserLimitLogMapper, OtcUserLimitLog> implements OtcUserLimitLogService {
}
