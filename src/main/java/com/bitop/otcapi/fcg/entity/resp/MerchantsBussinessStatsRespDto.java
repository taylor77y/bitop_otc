package com.bitop.otcapi.fcg.entity.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@ToString
public class MerchantsBussinessStatsRespDto {

//    @ApiModelProperty(value = "商户OTC昵称")
//    private String advertisingName;

    @ApiModelProperty(value = "月成功成交笔数")
    private Integer monthlySuccessCount;

    @ApiModelProperty(value = "月成功成交几率")
    private Integer monthlySuccessPercent;

    @ApiModelProperty(value = "总成交笔数")
    private Integer grandTotal;

    @ApiModelProperty(value = "总买入资产（计算买入的数字货币）")
    private BigDecimal totalpurchasedAssets;

    @ApiModelProperty(value = "总卖出资产（计算卖出的数字货币）")
    private BigDecimal totalSoldAssets;


    @ApiModelProperty(value = "注册时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registrationTime;

    @ApiModelProperty(value = "保证金")
    private BigDecimal margin;

    @ApiModelProperty(value = "实名认证状态 状态：0:已认证 1：未认证 ")
    private String kycStatus;

    @ApiModelProperty(value = "高级认证状态 状态：0:已认证 1：未认证 ")
    private String advertisingStatus;
}
