package com.bitop.otcapi.fcg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitop.otcapi.fcg.entity.OtcCoinType;
import org.apache.ibatis.annotations.Param;

public interface OtcCoinTypeMapper extends BaseMapper<OtcCoinType> {

    int updateStatusById(@Param("id")String id, @Param("status")String status);
}
