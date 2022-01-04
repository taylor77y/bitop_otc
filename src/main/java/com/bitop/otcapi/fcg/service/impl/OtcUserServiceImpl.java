package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.fcg.entity.OtcUser;
import com.bitop.otcapi.fcg.mapper.OtcUserMapper;
import com.bitop.otcapi.fcg.service.OtcUserService;
import org.springframework.stereotype.Service;

@Service
public class OtcUserServiceImpl extends ServiceImpl<OtcUserMapper, OtcUser> implements OtcUserService {
}
