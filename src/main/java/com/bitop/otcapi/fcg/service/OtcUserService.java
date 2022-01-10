package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcUser;
import com.bitop.otcapi.fcg.entity.req.JwtAuthenticationRequest;

import java.util.Map;

public interface OtcUserService extends IService<OtcUser> {

    /**
     * 用户登录
     * @param
     * @return
     */
    Map<String,String> login(JwtAuthenticationRequest jwtAuthenticationRequest);
}
