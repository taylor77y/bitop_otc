package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.fcg.entity.OtcAdvertisingBusiness;
import com.bitop.otcapi.fcg.entity.req.OtcSettingReqDto;
import com.bitop.otcapi.fcg.mapper.OtcAdvertisingBusinessMapper;
import com.bitop.otcapi.fcg.service.OtcAdvertisingBusinessService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.util.EncoderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OtcAdvertisingBusinessServiceImpl extends ServiceImpl<OtcAdvertisingBusinessMapper, OtcAdvertisingBusiness> implements OtcAdvertisingBusinessService {

    @Autowired
    private OtcAdvertisingBusinessMapper ezAdvertisingBusinessMapper;

    /***
     * @Description: 完善otc交易信息
     * @Param: [otcSettingReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2021/12/30
     */
    @Override
    @Transactional(value="transactionManager1", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response otcSetting(OtcSettingReqDto otcSettingReqDto) {
        String name = otcSettingReqDto.getAdvertisingName();
        String securityPassword = otcSettingReqDto.getSecurityPassword();
        String userId = ContextHandler.getUserId();
        //判断是否修改过
        LambdaQueryWrapper<OtcAdvertisingBusiness> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OtcAdvertisingBusiness::getUserId, userId);
        OtcAdvertisingBusiness advertisingBusiness = baseMapper.selectOne(queryWrapper);
        if (advertisingBusiness.getSecurityPassword() != null) {
            return Response.error("OTC信息不能进行修改");
        }
//        Stopwatch stopwatch = Stopwatch.createStarted();
        advertisingBusiness.setSecurityPassword(EncoderUtil.encode(securityPassword));
//        stopwatch.stop(); // optional
//        long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
//        log.info("time: " + stopwatch); // formatted string like "12.3 ms"

        Integer exist = ezAdvertisingBusinessMapper.existByAdvertisingName(name);

        if(!ObjectUtils.isEmpty(exist)){
            // 当存在时，执行这里的代码
            return Response.error("昵称重复，请重新输入");
        }
        else{
            // 当不存在时，执行这里的代码
        }
        advertisingBusiness.setAdvertisingName(name);
        baseMapper.updateById(advertisingBusiness);
        return Response.success();
    }
}
