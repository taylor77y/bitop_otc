package com.bitop.otcapi.fcg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitop.otcapi.fcg.entity.OtcConfig;
import com.bitop.otcapi.fcg.entity.OtcOrderIndex;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OtcOrderIndexMapper extends BaseMapper<OtcOrderIndex> {

    OtcOrderIndex getOneByName(@Param("name")String name);
}
