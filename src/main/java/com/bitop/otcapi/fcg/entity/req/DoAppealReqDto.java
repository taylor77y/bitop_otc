package com.bitop.otcapi.fcg.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DoAppealReqDto {
    @ApiModelProperty(value = "申诉id")
    @NotBlank(message = "申诉编号不能为空")
    private String id;

    @ApiModelProperty(value = "申诉操作： 3：申诉失败 4：申诉成功)")
    @NotBlank(message = "申诉状态不能为空")
    private String status;

    @ApiModelProperty(value = "处理结果")
    @NotBlank(message = "处理结果不能为空")
    private String memo;

}
