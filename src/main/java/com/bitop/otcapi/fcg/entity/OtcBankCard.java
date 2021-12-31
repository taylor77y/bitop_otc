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

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("otc_bank_card")
@ApiModel(value="OtcBankCard对象", description="用户绑定的银行卡")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OtcBankCard extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id",required = true)
    @Size(min=1, max = 32)
    @NotBlank
    private String userId;

    @ApiModelProperty(value = "持卡人姓名",required = true)
    @Size(min=1, max = 32)
    private String accountName;

    @ApiModelProperty(value = "银行中文名称",required = true)
    @Size(min=1, max = 32)
    private String bankName;

    @ApiModelProperty(value = "银行英文缩写编码",required = true)
    @Size(min=1, max = 10)
    @NotBlank
    private String bankCode;

    @ApiModelProperty(value = "银行卡号",required = true)
    @Min(value = 15, message = "card number should not be less than 15")
    @Max(value = 17, message = "card number not be greater than 17")
    @NotBlank
    private String number;

    @ApiModelProperty(value = "状态(0:已激活 1：未激活)",required = true)
    private short status;
}
