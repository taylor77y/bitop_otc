package com.bitop.otcapi.fcg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitop.otcapi.fcg.entity.OtcAdvertisingBusiness;
import org.apache.ibatis.annotations.Param;

public interface OtcAdvertisingBusinessMapper extends BaseMapper<OtcAdvertisingBusiness> {

    Integer existByAdvertisingName(@Param("advertisingName")String advertisingName);

    //根据用户查询到OTC详情
    OtcAdvertisingBusiness selectOneByUserId(@Param("userId")String userId);
}
