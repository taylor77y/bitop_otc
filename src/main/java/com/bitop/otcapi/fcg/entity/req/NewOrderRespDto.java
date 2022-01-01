package com.bitop.otcapi.fcg.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NewOrderRespDto {

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "币种类型")
    private String coinName;

    @ApiModelProperty(value = "剩余数量")
    private BigDecimal lastAmount;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "广告类型(0:买  1：卖)")
    private String type;

    @ApiModelProperty(value = "卖家昵称")
    private String advertisingName;

    @ApiModelProperty(value = "页码")
    private Long page;

    @ApiModelProperty(value = "页容量")
    private Long limit;


}