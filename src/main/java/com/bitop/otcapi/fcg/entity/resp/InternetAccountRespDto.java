package com.bitop.otcapi.fcg.entity.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InternetAccountRespDto {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "网络账号姓名")
    private String internetAccountName;

    @ApiModelProperty(value = "网络账号号码")
    private String internetAccountNumber;

    @ApiModelProperty(value = "网络账号类型")
    private short internetAccountType;
}
