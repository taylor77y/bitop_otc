package com.bitop.otcapi.fcg.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 操作资产VO类
 *
 */
@Data
public class BalanceChange {
    /**用户ID*/
    private String userId;
    /**币种名称(大写)*/
    private String coinName;
    /**操作余额(正数加余额/负数减余额)**/
    private BigDecimal available;
    /**操作冻结(正数加冻结/负数减冻结)**/
    private BigDecimal frozen;
    /**操作锁仓(正数加锁仓/负数减锁仓)**/
    private BigDecimal lockup;
    /**手续费**/
    private BigDecimal fee;
    /**收支类型**/
    private String incomeType;
    /**操作主类型**/
    private String mainType;
    /**操作子类型**/
    private String sonType;
    /**备注**/
    private String memo;
}
