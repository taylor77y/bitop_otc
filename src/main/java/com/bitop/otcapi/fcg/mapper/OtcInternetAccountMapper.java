package com.bitop.otcapi.fcg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitop.otcapi.fcg.entity.OtcInternetAccount;
import com.bitop.otcapi.fcg.entity.req.InternetAccountReqDto;

/**
 * <p>
 * 用户 支付宝信息 Mapper 接口
 * </p>
 *
 * @author taylor
 * @since 2021-12-03
 */
public interface OtcInternetAccountMapper extends BaseMapper<OtcInternetAccount> {


    int save(OtcInternetAccount otcInternetAccount);

    int updateById(OtcInternetAccount otcInternetAccount);

    void updateStatusById(OtcInternetAccount otcInternetAccount);
}
