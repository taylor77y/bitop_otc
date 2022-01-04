package com.bitop.otcapi.fcg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitop.otcapi.fcg.entity.OtcOrderIndex;
import com.bitop.otcapi.fcg.entity.OtcOrderPayment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OtcOrderPaymentMapper extends BaseMapper<OtcOrderPayment> {
}
