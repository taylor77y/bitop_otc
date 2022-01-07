package com.bitop.otcapi.fcg.controller;

import com.bitop.otcapi.fcg.entity.CoinAccount;
import com.bitop.otcapi.fcg.entity.SearchModel;
import com.bitop.otcapi.fcg.service.CoinAccountService;
import com.bitop.otcapi.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "资产账户模块")
@RequestMapping("/otc/coinaccount")
public class CoinAccountController {

    @Autowired
    private CoinAccountService accountService;

    @ApiOperation(value = "资产列表")
    @PostMapping("/accountList")
//    @AuthToken
    public ResponsePageList<CoinAccount> accountList(@RequestBody SearchModel<CoinAccount> searchModel){
        return ResponsePageList.success(accountService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }
}
