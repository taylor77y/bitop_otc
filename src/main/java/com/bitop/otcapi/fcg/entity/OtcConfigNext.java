package com.bitop.otcapi.fcg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("otc_config_next")
@ApiModel(value="OtcConfig对象", description="次级菜单-OTC配置")
public class OtcConfigNext extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private long id;

    @ApiModelProperty(value = "卖方KYC等级 0:无认证 1：基础认证 2：高级认证")
    private short sellKycLevel;

    @ApiModelProperty(value = "买方KYC等级 0:无认证 1：基础认证 2：高级认证")
    private short buyKycLevel;

    @ApiModelProperty(value = "支持的数字币币种",required = true)
    private String digitalCurrency;

    @ApiModelProperty(value = "法币币种",required = true)
    private String fiatCurrency;

    @ApiModelProperty(value = "平台处理费用")
    @Size(min = 10, max = 20, message
            = "fee must be between 10 and 20 int")
    private BigDecimal fee;

    @ApiModelProperty(value = "收款二维码类型id：1：银行  2：支付宝 3：微信 4：现金面交")
    @NotBlank(message = "请先选择收款方式")
    private Integer paymentMethodId;

    @ApiModelProperty(value = "限制提现天数")
    @NotBlank(message = "请先选择限制提现天数")
    @Max(100)
    @Min(1)
    private short limitWithdrawDays;
}