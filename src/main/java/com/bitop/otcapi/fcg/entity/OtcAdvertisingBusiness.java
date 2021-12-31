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
@ApiModel(value="OtcAdvertisingBusiness对象", description="OTC广告商户信息")
@TableName("otc_advertising_business")
public class OtcAdvertisingBusiness extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户号")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "卖家昵称")
    private String advertisingName;

    @ApiModelProperty(value = "安全密码")
    private String securityPassword;

    @ApiModelProperty(value = "卖单数量")
    private Integer sellCount;

    @ApiModelProperty(value = "买单数量")
    private Integer buyCount;

    @ApiModelProperty(value = "是否加V/广告权限 （0:已加V  1:未加V）")
    private String plusV;

    @ApiModelProperty(value = "总完成率")
    private Double finishRate;

    @ApiModelProperty(value = "买总完成率")
    private Double finishBuyRate;

    @ApiModelProperty(value = "平均放行时间 分钟")
    private Double averagePass;

    @ApiModelProperty(value = "乐观锁 请忽略它的存在")
    @Version
    private Integer version;

    @ApiModelProperty(value = "保证金")
    private BigDecimal margin;

}
