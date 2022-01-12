package com.bitop.otcapi.fcg.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 匹配日OTC订单
 * </p>
 *
 * @author wanglei
 * @since 2021-06-18
 */
@Data
@TableName("otc_order_match")
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="EzOtcOrderMatch对象", description="OTC匹配订单")
public class OtcOrderMatch extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "匹配订单号")
    @TableId(value = "order_match_no")
    private String orderMatchNo;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户OTC昵称")
    private String matchAdvertisingName;

    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "匹配到的发布订单号")
    private String orderNo;

    @ApiModelProperty(value = "广告发布订单用户id")
    private String otcOrderUserId;

/*    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;*/

    @ApiModelProperty(value = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

    @ApiModelProperty(value = "完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishTime;

    @ApiModelProperty(value = "商户OTC昵称")
    private String advertisingName;

    @ApiModelProperty(value = "数量")
    private BigDecimal amount;

    @ApiModelProperty(value = "总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "国际货币")
    private String currencyCode;

    @ApiModelProperty(value = "订单状态(1:接单已取消 2:待接单 3：已取消 4：等待支付 5：已支付 6：已完成 7:申诉)")
    private String status;

    @ApiModelProperty(value = "币种类型")
    private String coinName;

    @ApiModelProperty(value = "订单到期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueTime;

    @ApiModelProperty(value = "乐观锁")
    @Version
    private Integer version;

    @ApiModelProperty(value = "订单类型：1：普通 2：一键")
    private String orderType;

    @ApiModelProperty(value = "交易类型(0:买  1：卖)")
    private String type;

    @ApiModelProperty(value = "支付详情id")
    private String orderPaymentId;

    @ApiModelProperty(value = "是否为接单广告(0:是 1：否)")
    private String isAdvertising;

    @ApiModelProperty(value = "是否有申诉 (0:有 1：无)")
    private String isAppeal;

}
