package com.bitop.otcapi.fcg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitop.otcapi.fcg.entity.OtcOrderIndex;
import com.bitop.otcapi.fcg.entity.OtcOrderMatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OtcOrderMatchMapper extends BaseMapper<OtcOrderMatch> {

    Integer existNnfinishedOrderByTypeAndStatus(@Param("orderMatchNo")String orderMatchNo, @Param("status") String status);
}
