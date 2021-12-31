package com.bitop.otcapi.fcg.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OtcSettingReqDto {
    @ApiModelProperty(value = "卖家昵称",required = true)
    @NotBlank(message = "{advertising.name.not}")
    private String advertisingName;


    @ApiModelProperty(value = "资金密码",required = true)
    @NotBlank(message = "{security.password.not}")
    private String securityPassword;
}
