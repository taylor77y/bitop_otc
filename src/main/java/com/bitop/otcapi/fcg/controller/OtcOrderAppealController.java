package com.bitop.otcapi.fcg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bitop.otcapi.aspectj.lang.annotation.AuthToken;
import com.bitop.otcapi.aspectj.lang.annotation.Log;
import com.bitop.otcapi.constant.OperatorType;
import com.bitop.otcapi.fcg.entity.OtcOrderAppeal;
import com.bitop.otcapi.fcg.entity.SearchModel;
import com.bitop.otcapi.fcg.entity.req.AppealReqDto;
import com.bitop.otcapi.fcg.entity.req.DoAppealReqDto;
import com.bitop.otcapi.fcg.entity.req.DoOrderReqDto;
import com.bitop.otcapi.fcg.service.OtcOrderAppealService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponseList;
import com.bitop.otcapi.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@Api(tags = "OTC-订单申诉模块")
@RequestMapping("/otc/orderappeal")
public class OtcOrderAppealController {

    @Autowired
    private OtcOrderAppealService appealService;

//    @NoRepeatSubmit
    @ApiOperation(value = "提起订单申诉")
    @PutMapping("appeal")
//    @AuthToken
//    @Log(title = "订单申诉", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public Response appeal(@RequestBody @Valid  AppealReqDto appealReqDto) {
        return appealService.appeal(appealReqDto);
    }


    @ApiOperation(value = "根据订单号查询申诉详情")
    @ApiImplicitParam(name = "orderMatchNo",value = "orderMatchNo",required = true)
//    @AuthToken
    @GetMapping("appealInfo/{orderMatchNo}")
    public ResponseList<OtcOrderAppeal> appealInfo(@PathVariable String orderMatchNo){
        LambdaQueryWrapper<OtcOrderAppeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(OtcOrderAppeal::getOrderMatchNo,orderMatchNo);
        return ResponseList.success(appealService.list(queryWrapper));
    }

//    @NoRepeatSubmit
    @ApiOperation(value = "取消申诉")
    @ApiImplicitParam(name = "id",value = "id",required = true)
    @PutMapping("cancelAppeal/{id}")
//    @AuthToken
//    @Log(title = "取消申诉", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public Response cancelAppeal(@PathVariable String id) {
        return appealService.cancelAppeal(id);
    }

    @ApiOperation(value = "查询投诉列表")
    @PostMapping("appealList")
    @AuthToken
    public ResponsePageList<OtcOrderAppeal> appealList(@RequestBody SearchModel<OtcOrderAppeal> searchModel) {
        return ResponsePageList.success(appealService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @ApiOperation(value = "处理投诉")
    @PostMapping("doAppeal")
    @AuthToken
    @Log(title = "OTC模块", logInfo ="处理投诉", operatorType = OperatorType.MANAGE)
    public Response doAppeal(@RequestBody @Validated DoAppealReqDto doAppealReqDto) {
        return appealService.doAppeal(doAppealReqDto);
    }

    @ApiOperation(value = "处理投诉后修改订单状态")
    @PutMapping("doOrder")
    @AuthToken
    @Log(title = "OTC模块", logInfo ="处理投诉后修改订单状态", operatorType = OperatorType.MANAGE)
    public Response doOrder(@RequestBody @Validated DoOrderReqDto orderReqDto) {
        return appealService.doOrder(orderReqDto);
    }
}
