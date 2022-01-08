package com.bitop.otcapi.fcg.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SysLogininfor对象", description="登录日志")
@TableName("sys_logininfor")
public class SysLogininfor implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "访问ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户账号")
    private String userName;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "登录IP地址")
    private String ipaddr;

    @ApiModelProperty(value = "登录地点")
    private String loginLocation;

    @ApiModelProperty(value = "浏览器类型")
    private String browser;

    @ApiModelProperty(value = "操作系统")
    private String os;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


}
