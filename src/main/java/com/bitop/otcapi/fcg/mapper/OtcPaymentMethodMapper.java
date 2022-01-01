package com.bitop.otcapi.fcg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitop.otcapi.fcg.entity.OtcBankCard;
import com.bitop.otcapi.fcg.entity.OtcCoinType;
import com.bitop.otcapi.fcg.entity.OtcPaymentMethod;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OtcPaymentMethodMapper extends BaseMapper<OtcPaymentMethod> {

    int save(OtcPaymentMethod otcPaymentMethod);

    int updateById(OtcPaymentMethod otcPaymentMethod);
}
