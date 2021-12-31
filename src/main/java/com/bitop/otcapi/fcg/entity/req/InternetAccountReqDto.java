package com.bitop.otcapi.fcg.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InternetAccountReqDto {

    @ApiModelProperty(value = "id null：增加  不为null：修改")
    private String id;

    @ApiModelProperty(value = "网络账号姓名")
    private String internetAccountName;

    @ApiModelProperty(value = "网络账号号码")
    private String internetAccountNumber;

    @ApiModelProperty(value = "网络账号类型")
    private short internetAccountType;

    @ApiModelProperty(value = "状态(0:已激活 1：未激活)")
    private short status;
}
