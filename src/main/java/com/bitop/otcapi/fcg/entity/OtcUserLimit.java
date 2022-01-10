package com.bitop.otcapi.fcg.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("otc_user_limit")
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="OtcUserLimit对象", description="用户封号表")
public class OtcUserLimit extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // Regex for acceptable usernames
    public static final String USERNAME_REGEX = "^[_'.@A-Za-z0-9-]*$";

    @ApiModelProperty(value = "用户id")
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private String userId;

    @ApiModelProperty(value = "用户名")
    @NotNull
    @Pattern(regexp = USERNAME_REGEX)
    @Size(min = 1, max = 50)
    private String userName;

    @ApiModelProperty(value = "登录状态  0：未封号 1：已封号")
    private String login;

    @ApiModelProperty(value = "提现状态  0：未封号 1：已封号")
    private String withdraw;

    @ApiModelProperty(value = "发布广告封禁 提现状态  0：未封号 1：已封号")
    private String orders;

    @ApiModelProperty(value = "买卖封禁 提现状态  0：未封号 1：已封号")
    private String business;

/*    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;*/


}
