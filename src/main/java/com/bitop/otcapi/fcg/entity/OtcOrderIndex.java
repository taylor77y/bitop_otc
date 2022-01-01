package com.bitop.otcapi.fcg.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@TableName("otc_order_index")
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="OtcOrderIndex", description="广告订单号自增表")
public class OtcOrderIndex extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "标志自增长的信息名字")
    @TableId
    private String name;

    @ApiModelProperty(value = "乐观锁")
    @Version
    private Integer version;

    @ApiModelProperty(value = "信息增长值")
    private Integer step;

    @ApiModelProperty(value = "所属订单模块")
    private Integer other;

    @ApiModelProperty(value = "我们要获取的信息值")
    private Integer currentValue;

}