package com.bitop.otcapi.fcg.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("otc_order")
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="OtcOrder对象", description="广告订单表")
public class OtcOrder extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单号")
    @Size(min = 5, max = 50, message
            = "orderNo must be between 10 and 200 characters")
    @TableId(value = "order_no")
    private String orderNo;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "总数量")
    @Size(min = 10, max = 100000, message
            = "totalAmount must be between 10 and 100000 amount")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "匹配数量")
    private BigDecimal quotaAmount;

    @ApiModelProperty(value = "冻结数量")
    private BigDecimal frozeAmount;

    @ApiModelProperty(value = "国际货币")
    private String currencyCode;

    @ApiModelProperty(value = "币种类型")
    private String coinName;

    @ApiModelProperty(value = "最小限额")
    @Size(min = 10, max = 200, message
            = "minimumLimit must be between 10 and 200 characters")
    private BigDecimal minimumLimit;

    @ApiModelProperty(value = "最大限额")
    private BigDecimal maximumLimit;

    @ApiModelProperty(value = "广告类型(0:买  1：卖)")
    private Integer type;

    @ApiModelProperty(value = "是否为接单广告(0:是 1：否)")
    private Integer isAdvertising;

    @ApiModelProperty(value = "支付方式1ID")
    private Integer paymentMethod1;

    @ApiModelProperty(value = "支付方式2ID")
    private Integer paymentMethod2;

    @ApiModelProperty(value = "支付方式3ID")
    private Integer paymentMethod3;

    @ApiModelProperty(value = "上架昵称")
    private String advertisingName;

//    @ApiModelProperty(value = "真实姓名")
//    private String realName;

    @ApiModelProperty(value = "付款期限(分钟)")
    private Integer prompt;

    @ApiModelProperty(value = "交易提示")
    private String tradingTips;

    @ApiModelProperty(value = "订单状态（0：正常 1：已下架）")
    private Integer status;

    @ApiModelProperty(value = "订单完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

/*    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;*/

    @ApiModelProperty(value = "乐观锁")
    @Version
    private Integer version;
}
