package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.fcg.entity.OtcOrderMatch;
import com.bitop.otcapi.fcg.mapper.OtcOrderMatchMapper;
import com.bitop.otcapi.fcg.service.OtcOrderMatchService;
import org.springframework.stereotype.Service;

@Service
public class OtcOrderMatchServiceImpl extends ServiceImpl<OtcOrderMatchMapper, OtcOrderMatch> implements OtcOrderMatchService {
}
