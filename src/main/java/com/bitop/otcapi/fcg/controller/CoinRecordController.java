package com.bitop.otcapi.fcg.controller;

import com.bitop.otcapi.fcg.entity.CoinRecord;
import com.bitop.otcapi.fcg.entity.SearchModel;
import com.bitop.otcapi.fcg.service.CoinRecordService;
import com.bitop.otcapi.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "资产流水模块")
@RequestMapping("/otc/coinrecord")
public class CoinRecordController {

    @Autowired
    private CoinRecordService recordService;

    @ApiOperation(value = "资产流水")
//    @AuthToken
    @PostMapping("assetTurnover")
    public ResponsePageList<CoinRecord> assetTurnover(@RequestBody SearchModel<CoinRecord> searchModel) {
        return ResponsePageList.success(recordService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }
}
