package com.bitop.otcapi.fcg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitop.otcapi.fcg.entity.CoinType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CoinTypeMapper extends BaseMapper<CoinType> {

    int save(CoinType otcCoinType);

    int updateById(CoinType otcCoinType);

    int updateStatusById(@Param("id")String id, @Param("status")String status);
}
