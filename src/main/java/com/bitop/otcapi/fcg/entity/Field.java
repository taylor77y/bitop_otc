package com.bitop.otcapi.fcg.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Field implements Serializable {

    @ApiModelProperty(value = "查询字段名")
    private String name;

    @ApiModelProperty(value = "查询字段值")
    private String value;

    @ApiModelProperty(value = "查询方式 eq：精确   like：模糊")
    private QueryMethod queryMethod;
}
