package com.bitop.otcapi.fcg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("otc_config")
@ApiModel(value="OtcConfig对象", description="次级菜单-OTC配置")
public class OtcConfig extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "每日最大取消数量")
    @Size(min = 10, max = 20, message
            = "maxCancelNum must be between 10 and 200 int")
    private Integer maxCancelNum;

    @ApiModelProperty(value = "付款最大期限（分钟）")
    @Size(min = 20, max = 50, message
            = "maxPayTime must be between 20 and 50 int")
    private Integer maxPayTime;

    @ApiModelProperty(value = "付款最小期限（分钟）")
    @Size(min = 1, max = 10, message
            = "minPayTime must be between 1 and 10 minutes")
    private Integer minPayTime;

    @ApiModelProperty(value = "商户保证金")
    @Size(min = 10, max = 5000, message
            = "advertisingBusinessMargin must be between 10 and 5000 amount")
    private Integer advertisingBusinessMargin;

    @ApiModelProperty(value = "接单时间限制（分钟)")
    @NotBlank(message = "请先选择限制接单时间（分钟）")
    @Size(min = 1, max = 50, message
            = "orderTime must be between 1 and 50 minutes")
    private Integer orderTime;
}
