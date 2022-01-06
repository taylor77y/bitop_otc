package com.bitop.otcapi.fcg.entity.resp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CoinConfigRespDto {

    @ApiModelProperty(value = "id")
    private int id;

    @ApiModelProperty(value = "币种id")
    private int coinId;

    @ApiModelProperty(value = "币种名称")
    private String coinName;

    @ApiModelProperty(value = "十进制金额")
    private int amountDecimal;

    @ApiModelProperty(value = "卖出广告最低发布数量")
    private double minTranscationAmount;

    @ApiModelProperty(value = "最大交易数量")
    private double maxTranscationAmount;

    @ApiModelProperty(value = "手续费")
    private double fee;

    @ApiModelProperty(value = "状态 （0启用 1禁用 ）")
    private int status;

    @ApiModelProperty(value = "停止销售（0启用 1禁用 ）")
    private int stopSelling;

    @ApiModelProperty(value = "禁止购买（0启用 1禁用 ）")
    private int prohibitionPurchase;
}