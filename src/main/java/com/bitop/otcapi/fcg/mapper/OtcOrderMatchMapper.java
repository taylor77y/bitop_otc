package com.bitop.otcapi.fcg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitop.otcapi.fcg.entity.OtcOrderMatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OtcOrderMatchMapper extends BaseMapper<OtcOrderMatch> {

    OtcOrderMatch existUnfinishedOrderByTypeAndStatus(@Param("userId")String userId, @Param("type") String type,
                                                      @Param("statusArray") String[] statusArray);

    List<OtcOrderMatch> existUnfinishedOrderByNoAndStatus(@Param("orderNo")String orderNo, @Param("statusArray") String[] statusArray);
}
