package com.bitop.otcapi.fcg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitop.otcapi.fcg.entity.OtcOrderMatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OtcOrderMatchMapper extends BaseMapper<OtcOrderMatch> {

    OtcOrderMatch existNnfinishedOrderByTypeAndStatus(@Param("userId")String userId, @Param("type") String type, @Param("statusArray") String[] statusArray);
}
