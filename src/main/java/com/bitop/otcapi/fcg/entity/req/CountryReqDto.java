package com.bitop.otcapi.fcg.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CountryReqDto {
    @ApiModelProperty(value = "国家中文名",required = true)
    @NotBlank(message = "国家中文名不能为空")
    private String countryName;

    @ApiModelProperty(value = "国旗地址",required = true)
    @NotBlank(message = "请先上传国旗图片")
    private String nationalFlagAddr;

    @ApiModelProperty(value = "国家英文名",required = true)
    @NotBlank(message = "国家英文名不能为空")
    private String countryNameEn;

    @ApiModelProperty(value = "国家编号",required = true)
    @NotBlank(message = "国家编号不能为空")
    private String countryCode;

    @ApiModelProperty(value = "货币代码")
    private String currencyCode;

    @ApiModelProperty(value = "国家电话区号",required = true)
    @NotBlank(message = "国家电话区号不能为空")
    private String countryTelCode;
}
