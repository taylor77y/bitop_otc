package com.bitop.otcapi.fcg.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="登录用户", description="用户表")
@TableName("otc_user")
public class OtcUser extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // Regex for acceptable usernames
    public static final String USERNAME_REGEX = "^[_'.@A-Za-z0-9-]*$";

    @ApiModelProperty(value = "用户编号")
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private String userId;

    @ApiModelProperty(value = "父级编号")
    private String parentId;

    @NotNull
    @Pattern(regexp = USERNAME_REGEX)
    @Size(min = 1, max = 50)
    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "电话")
    @Size(min = 5, max = 15, message
            = "phone number must be between 5 and 15 characters")
    private String phone;

    @ApiModelProperty(value = "电话国际区号")
    @Size(min = 1, max = 5, message
            = "telephone international area code must be between 10 and 200 characters")
    private String phoneArea;

    @ApiModelProperty(value = "国家编号")
    private String countryCode;

    @ApiModelProperty(value = "邮箱")
    @Email
    private String email;

    @ApiModelProperty(value = "邀请码")
    private String inviteCode;//----------------------

    @ApiModelProperty(value = "高级认证：0：已认证 1：未认证")
    private Integer level;

    @ApiModelProperty(value = "状态：0已认证 1未认证")
    private Integer kycStatus;

    @ApiModelProperty(value = "状态 0 正常 1禁止")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    @TableLogic(value = "0",delval = "1")
    private Integer isDeleted;

    @ApiModelProperty(value = "最后登录IP")//mysql IP 保存为整数，使用mysql函数
    private String loginIp;

    @ApiModelProperty(value = "最后登录时间")
    private LocalDateTime loginDate;

    @ApiModelProperty(value = "登录状态  0：未封号 1：已封号")
    @TableField(exist = false)
    private Integer login;

    @ApiModelProperty(value = "提现状态  0：未封号 1：已封号")
    @TableField(exist = false)
    private Integer withdraw;

    @ApiModelProperty(value = "发布广告封禁 提现状态  0：未封号 1：已封号")
    @TableField(exist = false)
    private Integer orders;

    @ApiModelProperty(value = "买卖封禁 提现状态  0：未封号 1：已封号")
    @TableField(exist = false)
    private Integer business;


}
