package com.bitop.otcapi.fcg.entity.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FiatCoinTypeRespDto {

    @ApiModelProperty(value = "法币英文代码")
    private String currencyCode;

    @ApiModelProperty(value = "法币符号")
    private Integer currencySymbol;

    @ApiModelProperty(value = "法币比例")
    private String currencyScale;

    @ApiModelProperty(value = "法币对应的国家代码")
    private Integer countryCode;

    @ApiModelProperty(value = "法币图标url链接地址")
    private Integer iconUrl;
}
