package com.bitop.otcapi.fcg.entity.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BankCardRespDto {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "银行中文名称")
    private String bankName;

    @ApiModelProperty(value = "银行英文缩写编码")
    private String bankCode;

    @ApiModelProperty(value = "银行卡号")
    private String number;

    @ApiModelProperty(value = "状态(0:已激活 1：未激活)")
    private String status;
}
