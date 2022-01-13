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

/**
 * <p>
 *       网络账号表
 * </p>
 *
 * @author taylor
 * @since 2021-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("otc_internet_account")
@ApiModel(value="InternetAccount对象", description="网络账号表")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OtcInternetAccount extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id",required = true)
    @NotBlank
    private String userId;

    @ApiModelProperty(value = "网络账号名",required = true)
    @NotBlank
    private String internetAccountName;

    @ApiModelProperty(value = "网络账号号码",required = true)
    @NotBlank
    @Size(min=1, max = 32)
    private String internetAccountNumber;

    @ApiModelProperty(value = "网络账号类型 0微信 1QQ 2支付宝 3微博 4抖音 5陌陌 6豆瓣 7百度贴吧 8小红书 9优酷 10大众点评 11钉钉 12华为id 13apple id",required = true)
    private Integer internetAccountType;

    @ApiModelProperty(value = "状态(0:已激活 1：未激活)",required = true)
    @NotBlank(message = "请先选择状态")
    private Integer status;
}
