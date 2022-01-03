package com.bitop.otcapi.fcg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitop.otcapi.fcg.entity.OtcBankCard;
import com.bitop.otcapi.fcg.entity.OtcOrderAppeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OtcOrderAppealMapper extends BaseMapper<OtcOrderAppeal> {

    Integer existAppealedByUserIdAndNo(@Param("orderMatchNo")String orderMatchNo,@Param("status") String status,@Param("userId") String userId);
}
