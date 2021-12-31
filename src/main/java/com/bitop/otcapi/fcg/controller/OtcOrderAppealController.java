package com.bitop.otcapi.fcg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bitop.otcapi.fcg.entity.OtcOrderAppeal;
import com.bitop.otcapi.fcg.entity.req.AppealReqDto;
import com.bitop.otcapi.fcg.service.OtcCoinTypeService;
import com.bitop.otcapi.fcg.service.OtcOrderAppealService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponseList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@Api(tags = "OTC-订单申诉模块")
@RequestMapping("/otc/internetaccount")
public class OtcOrderAppealController {

    @Autowired
    private OtcOrderAppealService appealService;

//    @NoRepeatSubmit
    @ApiOperation(value = "订单申诉")
    @PutMapping("appeal")
//    @AuthToken
//    @Log(title = "订单申诉", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public Response appeal(@RequestBody @Valid  AppealReqDto appealReqDto) {
        return appealService.appeal(appealReqDto);
    }


    @ApiOperation(value = "根据订单号查询申诉详情")
//    @AuthToken
    @GetMapping("appealInfo/{orderMatchNo}")
    public ResponseList<OtcOrderAppeal> appealInfo(@PathVariable String orderMatchNo){
        LambdaQueryWrapper<OtcOrderAppeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(OtcOrderAppeal::getOrderMatchNo,orderMatchNo);
        return ResponseList.success(appealService.list(queryWrapper));
    }

//    @NoRepeatSubmit
    @ApiOperation(value = "取消申诉")
    @PutMapping("cancelAppeal/{id}")
//    @AuthToken
//    @Log(title = "取消申诉", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public Response cancelAppeal(@PathVariable String id) {
        return appealService.cancelAppeal(id);
    }
}