package com.bitop.otcapi.fcg.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlaceOrderReqDto {

    @ApiModelProperty(value = "订单号")
    private String orderNo;//订单号

    @ApiModelProperty(value = "//购买/卖出 数量")
    private BigDecimal amount;//购买/卖出 数量


}
