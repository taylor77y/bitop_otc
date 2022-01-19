package com.bitop.otcapi.fcg.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AdMatchOrderQueryReqDto {

    @ApiModelProperty(value = "已完成订单：1  待处理订单 2")
    private String status;

    @ApiModelProperty(value = "发布单号")
    private String orderNo;

    @ApiModelProperty(value = "页码")
    private Long page;

    @ApiModelProperty(value = "页容量")
    private Long limit;
}
