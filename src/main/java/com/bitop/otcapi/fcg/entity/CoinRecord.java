package com.bitop.otcapi.fcg.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coin_record")
@ApiModel(value="CoinRecord对象", description="资产记录流水表")
public class CoinRecord extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "币种名")
    private String coinName;

    @ApiModelProperty(value = "收支类型（1收入 2支出）")
    private String incomeType;

    @ApiModelProperty(value = "主类型")
    private String mainType;

    @ApiModelProperty(value = "子类型  1")
    private String sonType;

    @ApiModelProperty(value = "数量")
    private BigDecimal amount;

    @ApiModelProperty(value = "来自地址")
    private String fromAddress;

    @ApiModelProperty(value = "到达地址")
    private String toAddress;

    @ApiModelProperty(value = "交易ID")
    private String txid;

    @ApiModelProperty(value = "备注")
    private String memo;

    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "状态（1成功） 2 待审核  3审核通过 4审核拒绝")
    private String status;

}
