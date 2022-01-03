package com.bitop.otcapi.fcg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coin_account")
@ApiModel(value="CoinAccount对象", description="资产余额表")
public class CoinAccount extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "币种ID")
    private Integer coinId;

    @ApiModelProperty(value = "币种名")
    private String coinName;

    @ApiModelProperty(value = "余额")
    private BigDecimal available;

    @ApiModelProperty(value = "冻结")
    private BigDecimal frozen;

    @ApiModelProperty(value = "锁仓")
    private BigDecimal lockup;

    @ApiModelProperty(value = "版本号")
    @Version
    private Integer version;
}
