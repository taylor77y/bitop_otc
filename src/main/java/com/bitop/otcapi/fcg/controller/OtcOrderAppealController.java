package com.bitop.otcapi.fcg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bitop.otcapi.fcg.entity.OtcOrderAppeal;
import com.bitop.otcapi.fcg.entity.req.AppealReqDto;
import com.bitop.otcapi.fcg.service.OtcCoinTypeService;
import com.bitop.otcapi.fcg.service.OtcOrderAppealService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponseList;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

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
