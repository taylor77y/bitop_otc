package com.bitop.otcapi.fcg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bitop.otcapi.fcg.entity.OtcAdvertisingBusiness;
import com.bitop.otcapi.fcg.entity.resp.CoinConfigRespDto;
import com.bitop.otcapi.fcg.service.OtcAdvertisingBusinessService;
import com.bitop.otcapi.fcg.service.OtcCoinConfigService;
import com.bitop.otcapi.response.ResponseList;
import com.bitop.otcapi.util.MessageUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;

public class OtcCoinConfigController {

    @Autowired
    private OtcAdvertisingBusinessService advertisingBusinessService;

    @Autowired
    private OtcCoinConfigService otcCoinConfigService;

    /*
     *一般而言，我们把火币、云币等交易所称为场内交易平台，此类平台不仅帮助存放我们的人民币和数字货币，还提供买卖交易的场所。
     *而在场外交易平台上，卖家只将自己的数字资产(一般是比特币)暂存在平台上，
     *通过挂单撮合的方式进行交易，最大的不同便是比特币的买卖方式和过程由双方自行商定并进行，无需通过平台。
     */
    @ApiOperation(value = "查询所有 coin 挂单配置信息")
    @ApiImplicitParam(name = "transactionType",value = "transactionType",required = true)
//    @AuthToken
    @GetMapping("getAllOTCTransctionConfig/{transactionType}")// 交易类型：在线购买、卖出
    public ResponseList<CoinConfigRespDto> getAllOTCTransctionConfig(@PathVariable String transactionType, @PathVariable(value = "userId", required = false) String userId){
        LambdaQueryWrapper<OtcAdvertisingBusiness> businessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        businessLambdaQueryWrapper.eq(OtcAdvertisingBusiness::getUserId, userId);//ContextHandler.getUserId()
        OtcAdvertisingBusiness one = advertisingBusinessService.getOne(businessLambdaQueryWrapper);
        if (StringUtils.isEmpty(one.getSecurityPassword())) {
            return ResponseList.error(MessageUtils.message("请先完善otc交易信息"), Collections.EMPTY_LIST);
        }

        return ResponseList.success(otcCoinConfigService.getAllOTCTransctionConfig(transactionType));
    }
}
