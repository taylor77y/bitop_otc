package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.constant.LoginType;
import com.bitop.otcapi.exception.UserException;
import com.bitop.otcapi.exception.UserPasswordNotMatchException;
import com.bitop.otcapi.fcg.entity.OtcUser;
import com.bitop.otcapi.fcg.entity.req.JwtAuthenticationRequest;
import com.bitop.otcapi.fcg.mapper.OtcUserMapper;
import com.bitop.otcapi.fcg.service.OtcUserService;
import com.bitop.otcapi.mq.producer.LoginProducer;
import com.bitop.otcapi.security.JWTHelper;
import com.bitop.otcapi.security.JWTInfo;
import com.bitop.otcapi.util.EncoderUtil;
import com.bitop.otcapi.util.IpUtils;
import com.bitop.otcapi.util.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OtcUserServiceImpl extends ServiceImpl<OtcUserMapper, OtcUser> implements OtcUserService {

    @Autowired
    private JWTHelper jwtHelper;

    @Autowired
    private LoginProducer loginProducer;

    /**
     * 用户登录
     *
     * @param authenticationRequest
     * @return
     */
    @Override
    @Transactional//(value="transactionManager1")
    public Map<String, String> login(JwtAuthenticationRequest authenticationRequest) {
        //登录的时候 如果绑定了邮箱/电话号码 都可以用来登录
        LambdaQueryWrapper<OtcUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OtcUser::getPhone, authenticationRequest.getUsername()).or().eq(OtcUser::getEmail, authenticationRequest.getUsername());
        OtcUser ezUser = baseMapper.selectOne(lambdaQueryWrapper);
        //用户不存在
        if (ezUser == null) {
            throw new UserException("登录用户不存在", null);
        }
        /*LambdaQueryWrapper<EzUserLimitLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzUserLimitLog::getIsExpire, "0");
        queryWrapper.eq(EzUserLimitLog::getUserId, ezUser.getUserId());
        queryWrapper.eq(EzUserLimitLog::getType, LimitType.LOGINLIMIT.getCode());
        EzUserLimitLog one = limitLogService.getOne(queryWrapper);
        if (one != null) {
            if (one.getBanTime() != null && one.getBanTime().getTime() < DateUtils.getNowDate().getTime()) {
                one.setIsExpire("1");
                limitLogService.updateById(one);
                ezUser.setStatus("0");
                baseMapper.updateById(ezUser);
                LambdaUpdateWrapper<EzUserLimit> ezUserLimitLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                ezUserLimitLambdaUpdateWrapper.eq(EzUserLimit::getUserId, ezUser.getUserId());
                ezUserLimitLambdaUpdateWrapper.set(EzUserLimit::getLogin, 0);
                limitService.update(ezUserLimitLambdaUpdateWrapper);
            }else {
                throw new UserException("此账号已被封锁", null);
            }
        }*/
        //密码错误
        if (!EncoderUtil.matches(authenticationRequest.getPassword(), ezUser.getPassword())) {
            throw new UserPasswordNotMatchException();
        }
        //创建token
        JWTInfo jwtInfo = new JWTInfo(ezUser.getUserName(), ezUser.getUserId(), LoginType.APP.getType());
        String token = jwtHelper.createToken(jwtInfo);
        log.info("  require logging... >>userToken:{}<<", token);
        String userId = ezUser.getUserId();
        ezUser.setLoginDate(LocalDateTime.now());
        final String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        ezUser.setLoginIp(ip);
        baseMapper.updateById(ezUser);
        loginProducer.sendMsgLoginFollowUp(ezUser.getUserName(), userId, ezUser.getNickName(), LoginType.APP.getType());
        Map<String, String> map = new HashMap<>(2);
        map.put("token", token);
        map.put("userId", userId);
        return map;
    }
}
