package com.bitop.otcapi.fcg.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OtcConfigNextReqDto {

    @ApiModelProperty(value = "id null：增加  不为null：修改")
    private String id;

    @ApiModelProperty(value = "卖方KYC等级")
    private String sellKycLevel;

    @ApiModelProperty(value = "买方KYC等级")
    private String buyKycLevel;

    @ApiModelProperty(value = "数字币币种")
    private String digitalCurrency;

    @ApiModelProperty(value = "法币币种")
    private String fiatCurrency;

    @ApiModelProperty(value = "平台处理费用")
    @NotBlank(message = "申诉状态不能为空")
    private String fee;

    @ApiModelProperty(value = "收款方式",required = true)
    private String paymentMethod;

    @ApiModelProperty(value = "限制提现天数")
    private String limitWithdrawDays;

}
