package com.bitop.otcapi.fcg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("otc_payment_method")
@ApiModel(value="OtcPaymentMethod", description="用户收款方式")
public class OtcPaymentMethod extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "支付方式详情编号")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "收款二维码类型id：1：银行  2：支付宝 3：微信 4：现金面交")
    @NotBlank(message = "请先选择收款方式")
    private Integer paymentMethodId;

    @ApiModelProperty(value = "卖家真实姓名")
    private String realName;

    @ApiModelProperty(value = "开户银行名")
    private String bankName;

    @ApiModelProperty(value = "银行地址")
    private String optional;

    @ApiModelProperty(value = "账号")
    private String accountNumber;

    @ApiModelProperty(value = "付款二维码")
    private String paymentQrCode;

    @ApiModelProperty(value = "状态(0:已激活 1：未激活)")
    private String status;
}
