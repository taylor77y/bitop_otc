package com.bitop.otcapi.fcg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitop.otcapi.fcg.entity.OtcBankCard;
import com.bitop.otcapi.fcg.entity.OtcCoinType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OtcCoinTypeMapper extends BaseMapper<OtcCoinType> {

    int save(OtcCoinType otcCoinType);

    int updateById(OtcCoinType otcCoinType);

    int updateStatusById(@Param("id")String id, @Param("status")String status);
}
