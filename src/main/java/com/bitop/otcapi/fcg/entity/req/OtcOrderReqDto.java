package com.bitop.otcapi.fcg.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class OtcOrderReqDto {

    @ApiModelProperty(value = "用户id 后台发布订单时加上")
    private String userId;

    @ApiModelProperty(value = "单价",required = true)
    @NotNull(message = "{price.not}")
    private BigDecimal price;

    @ApiModelProperty(value = "总数量")
    @NotNull(message = "{totalAmount.not}")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "国际货币")
    private String currencyCode;

    @ApiModelProperty(value = "币种类型")
    @Min(value = 1, message = "coin name should not be less than 15")
    @Max(value = 17, message = "coin name not be greater than 17")
    private String coinName;

    @ApiModelProperty(value = "最小限额")
    @NotNull(message = "{minimumLimit.not}")
    private BigDecimal minimumLimit;

    @ApiModelProperty(value = "最大限额")
    @NotNull(message = "{maximumLimit.not}")
    private BigDecimal maximumLimit;

    @ApiModelProperty(value = "订单类型(0:买  1：卖)")
    private Integer type;

    @ApiModelProperty(value = "是否为接单广告(0:是 1：否)")
    private Integer isAdvertising;

    @ApiModelProperty(value = "支付方式1")
    private Integer paymentMethod1;

    @ApiModelProperty(value = "支付方式2")
    private Integer paymentMethod2;

    @ApiModelProperty(value = "支付方式3")
    private Integer paymentMethod3;

    @ApiModelProperty(value = "付款期限(分钟)")
    @NotNull(message = "{prompt.not}")
    private Integer prompt;

    @ApiModelProperty(value = "交易备注")
    @NotNull(message = "{tradingTips.not}")
    private String tradingTips;
}
