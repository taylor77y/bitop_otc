package com.bitop.otcapi.fcg.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DoOrderReqDto {
    @ApiModelProperty(value = "匹配订单号")
    @NotBlank(message = "匹配订单号不能为空")
    private String orderMatchNo;

    @ApiModelProperty(value = "申诉操作： 0：放行 1：订单失败)")
    @NotBlank(message = "请先选择放行还是订单失败")
    private String status;

}
