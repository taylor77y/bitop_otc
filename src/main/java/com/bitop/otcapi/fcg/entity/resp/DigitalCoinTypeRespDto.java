package com.bitop.otcapi.fcg.entity.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DigitalCoinTypeRespDto {

    @ApiModelProperty(value = "币种名")
    private String coinName;

    @ApiModelProperty(value = "币种状态（0启用（默认） 1禁用 ）")
    private String status;
}
