package com.bitop.otcapi.fcg.entity.resp;

import com.bitop.otcapi.fcg.entity.OtcOrderPayment;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class OrderRecordRespDto {
    @ApiModelProperty(value = "匹配订单号")
    private String orderMatchNo;

    @ApiModelProperty(value = "发布订单号")
    private String orderNo;

    @ApiModelProperty(value = "买家/卖家昵称")
    private String advertisingName;

    @ApiModelProperty(value = "订单状态(1:接单已取消 2:待接单 3：已取消 4：等待支付 5：已支付 6：已完成 7:拒接单)")
    private String status;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "币种类型")
    private String coinName;

    @ApiModelProperty(value = "国际货币")
    private String currencyCode;

    @ApiModelProperty(value = "订单类型：1：普通 2：一键")
    private String orderType;

    @ApiModelProperty(value = "交易类型(0:买  1：卖)")
    private String type;

    @ApiModelProperty(value = "数量")
    private BigDecimal amount;

    @ApiModelProperty(value = "总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "支付方式")
    private List<OtcOrderPayment> ezOtcOrderPayments;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "订单到期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueTime;

    @ApiModelProperty(value = "当前时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nowTime;

    @ApiModelProperty(value = "是否为接单广告(0:是 1：否)")
    private String isAdvertising;

    @ApiModelProperty(value = "是否有申诉 (0:有 1：无)")
    private String isAppeal;
}
