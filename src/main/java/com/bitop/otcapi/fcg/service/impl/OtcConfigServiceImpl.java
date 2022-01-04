package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.constant.RedisConstants;
import com.bitop.otcapi.exception.BaseException;
import com.bitop.otcapi.fcg.entity.OtcConfig;
import com.bitop.otcapi.fcg.mapper.OtcConfigMapper;
import com.bitop.otcapi.fcg.service.OtcConfigService;
import com.bitop.otcapi.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OtcConfigServiceImpl extends ServiceImpl<OtcConfigMapper, OtcConfig> implements OtcConfigService {

    @Autowired
    private RedisCache redisCache;

    @Override
    public void  checkOtcStatus(String userId,Integer maxCancelNum) {
        Object object = redisCache.getCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + userId);
        if (object != null && (Integer) object > maxCancelNum) {//5后面从配置数据库得到
            throw new BaseException("你今天取消次数超过上线,每天再来");
        }
    }
}
