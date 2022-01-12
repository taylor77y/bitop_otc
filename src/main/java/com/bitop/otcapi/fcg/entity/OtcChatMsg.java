package com.bitop.otcapi.fcg.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * otc聊天信息表 不保存到mysql, 而保存到mongodb
 * </p>
 *
 * @author taylor
 * @since 2022-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="OtcChatMsg对象", description="otc聊天信息表")
public class OtcChatMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "匹配订单号")
    private String orderMatchNo;

    @ApiModelProperty(value = "发送者id")
    private String sendUserId;

    @ApiModelProperty(value = "接收者id")
    private String receiveUserId;

    @ApiModelProperty(value = "发送时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "发送内容")
    private String sendText;

    @ApiModelProperty(value = "内容类型(0:图片 1：文字)")
    private String type;

    @ApiModelProperty(value = "是否是系统消息(0 :是 1：否)")
    private String isSystem;

}
