package com.bitop.otcapi.fcg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bitop.otcapi.fcg.entity.OtcChatMsg;
import com.bitop.otcapi.fcg.service.OtcChatMsgService;
import com.bitop.otcapi.response.ResponseList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "OTC-聊天记录模块")
@RequestMapping("/otc/ezOtcChatMsg")
public class OtcChatMsgController {

    @Autowired
    private OtcChatMsgService otcChatMsgService;


    @ApiOperation(value = "根据 匹配订单id查询聊天记录")
    @PostMapping("chatMsg/{orderMatchNo}")
//    @AuthToken
    public ResponseList<OtcChatMsg> advertisingBusinessList(@PathVariable String orderMatchNo) {
        return ResponseList.success(otcChatMsgService.pageByNo(orderMatchNo));
    }
}