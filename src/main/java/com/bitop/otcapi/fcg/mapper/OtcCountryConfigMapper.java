package com.bitop.otcapi.fcg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitop.otcapi.fcg.entity.OtcCountryConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OtcCountryConfigMapper extends BaseMapper<OtcCountryConfig> {

    Integer existByCountryNameAndCountryCode(@Param("countryName")String countryName, @Param("countryNameEn")String countryNameEn,
                                             @Param("countryCode")String countryCode, @Param("countryTelCode")String countryTelCode);
}
