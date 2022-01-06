package com.bitop.otcapi.fcg.controller;

import com.bitop.otcapi.fcg.entity.resp.BankCodeRespDto;
import com.bitop.otcapi.fcg.service.OtcBankCardService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponseList;
import com.bitop.otcapi.fcg.entity.req.BankCardReqDto;
import com.bitop.otcapi.fcg.entity.resp.BankCardRespDto;
import com.bitop.otcapi.util.BankCardUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Api(tags = "OTC-用户银行卡模块")
@RequestMapping("/otc/bankcard")
public class OtcBankCardController {


    private static final List<BankCodeRespDto> BANK_CODES = BankCardUtils.BANK_CODES.entrySet()
            .stream()
            .map(entry -> BankCodeRespDto.builder().code(entry.getKey()).name(entry.getValue()).build())
            .collect(Collectors.toList());

    @Autowired
    private OtcBankCardService otcBankCardService;

    @ApiOperation(value = "用户银行卡列表")
//    @AuthToken
    @GetMapping("userBankCardList")
    public ResponseList<BankCardRespDto> userBankCardList(){
        return ResponseList.success(otcBankCardService.userBankCardList("123456"));
    }


    @ApiOperation(
            value = "Get bankCodes",
            nickname = "getBankCodes",
            response = BankCodeRespDto.class,
            responseContainer = "List")
    @GetMapping("/bankcodes")
    public ResponseList<BankCodeRespDto> getBankCodes() {
        return ResponseList.success(BANK_CODES);
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
