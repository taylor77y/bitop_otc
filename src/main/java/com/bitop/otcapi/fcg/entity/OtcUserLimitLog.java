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
import java.time.LocalDateTime;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("otc_user_limit_log")
@ApiModel(value="EzUserLimitLog对象", description="封号日志记录表")
public class OtcUserLimitLog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // Regex for acceptable usernames
    public static final String USERNAME_REGEX = "^[_'.@A-Za-z0-9-]*$";

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户名")
    @NotNull
    @Pattern(regexp = USERNAME_REGEX)
    @Size(min = 1, max = 50)
    private String userName;

    @ApiModelProperty(value = "详细")
    private String detailed;

    @ApiModelProperty(value = "封禁类别（0：登录封禁 1：提现封禁 2：发布广告封禁 3：买卖封禁）")
    private String type;

    @ApiModelProperty(value = "是否过期 1（true）已过期 ，0（false）未过期")
    private String isExpire;

    /*@ApiModelProperty(value = "封号操作者")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;*/

    @ApiModelProperty(value = "封号到期时间（null：永久封号）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime banTime;

}