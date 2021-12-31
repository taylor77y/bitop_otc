package com.bitop.otcapi.fcg.controller;

import com.bitop.otcapi.fcg.service.OtcBankCardService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponseList;
import com.bitop.otcapi.fcg.entity.req.BankCardReqDto;
import com.bitop.otcapi.fcg.entity.resp.BankCardRespDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@Api(tags = "OTC-用户银行卡模块")
@RequestMapping("/otc/bankcard")
public class OtcBankCardController {


    @Autowired
    private OtcBankCardService otcBankCardService;

    @ApiOperation(value = "用户银行卡列表")
//    @AuthToken
    @GetMapping("userBankCardList")
    public ResponseList<BankCardRespDto> userBankCardList(){
        return ResponseList.success(otcBankCardService.userBankCardList("123456"));
    }


    @ApiOperation(value = "添加/修改 用户银行卡信息")
//    @AuthToken
    @PostMapping("addOrUpdateUserBankCard")
//    @Log(title = "添加/修改 用户银行卡信息", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response addOrUpdateUserBankCard(@RequestBody @Valid BankCardReqDto bankCardReqDto){
        return otcBankCardService.addOrUpdateUserBankCard(bankCardReqDto);
    }


    @ApiOperation(value = "修改用户 银行卡 状态")
//    @AuthToken
    @PostMapping("updateUserBankCardStatus")
//    @Log(title = "修改用户 银行卡 状态", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response updateUserBankCardStatus(@RequestBody @Valid BankCardReqDto bankCardReqDto){
        return otcBankCardService.updateUserBankCardStatus(bankCardReqDto);
    }
}
