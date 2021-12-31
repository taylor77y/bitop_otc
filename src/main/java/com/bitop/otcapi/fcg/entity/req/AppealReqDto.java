package com.bitop.otcapi.fcg.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AppealReqDto {
    @ApiModelProperty(value = "申诉理由")
    @NotBlank(message = "{reason.not}")
    private String reason;

    @ApiModelProperty(value = "匹配订单号")
    private String orderMatchNo;

    @ApiModelProperty(value = " 1:对方未付款 2：对方未放行 3:其他")
    private String type;

    @ApiModelProperty(value = "凭证地址")
    @NotBlank(message = "{voucher.not}")
    private String voucher;

}
