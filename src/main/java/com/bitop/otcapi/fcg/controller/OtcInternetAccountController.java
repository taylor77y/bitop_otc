package com.bitop.otcapi.fcg.controller;

import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.fcg.entity.req.InternetAccountReqDto;
import com.bitop.otcapi.fcg.entity.resp.InternetAccountRespDto;
import com.bitop.otcapi.fcg.service.OtcInternetAccountService;
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
@Api(tags = "OTC-网络账号模块")
@RequestMapping("/otc/internetaccount")
public class OtcInternetAccountController  {


    @Autowired
    private OtcInternetAccountService internetAccountService;

    @ApiOperation(value = "网络账号列表")
//    @AuthToken
    @GetMapping("internetAccountList")
    public ResponseList<InternetAccountRespDto> internetAccountList(){
        return ResponseList.success(internetAccountService.internetAccountList(ContextHandler.getUserId()));
    }


    @ApiOperation(value = "添加/修改 网络账号信息")
//    @AuthToken
    @PostMapping("addOrUpdateInternetAccount")
//    @Log(title = "添加/修改 用户网络账号信息", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response addOrUpdateInternetAccount(@RequestBody @Valid InternetAccountReqDto internetAccountReqDto){
        return internetAccountService.addOrUpdateInternetAccount(internetAccountReqDto);
    }

    @ApiOperation(value = "修改 网络账号状态")
//    @AuthToken
    @PostMapping("updateUserInternetAccountStatus")
//    @Log(title = "修改 网络账号状态", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response updateUserInternetAccountStatus(@RequestBody @Valid InternetAccountReqDto internetAccountReqDto){
        return internetAccountService.updateUserInternetAccountStatus(internetAccountReqDto);
    }

}
