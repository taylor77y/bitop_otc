package com.bitop.otcapi.fcg.controller;

import com.bitop.otcapi.fcg.entity.OtcOrderMatch;
import com.bitop.otcapi.fcg.entity.SearchModel;
import com.bitop.otcapi.fcg.service.OtcOrderMatchService;
import com.bitop.otcapi.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "OTC-匹配订单模块")
@RequestMapping("/otc/otcOrderMatch")
public class OtcOrderMatchController {

    @Autowired
    private OtcOrderMatchService orderMatchService;

//    @AuthToken
    @ApiOperation(value = "OTC 匹配订单列表")
    @PostMapping("otcOrderList")
    public ResponsePageList<OtcOrderMatch> otcOrderList(@RequestBody SearchModel<OtcOrderMatch> searchModel) {
        return ResponsePageList.success(orderMatchService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

}
