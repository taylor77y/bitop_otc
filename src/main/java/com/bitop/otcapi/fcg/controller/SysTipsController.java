package com.bitop.otcapi.fcg.controller;

import com.bitop.otcapi.fcg.entity.SearchModel;
import com.bitop.otcapi.fcg.entity.SysTips;
import com.bitop.otcapi.fcg.service.SysTipsService;
import com.bitop.otcapi.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "系统模块-站内信")
@RequestMapping("/system/sys-tips")
public class SysTipsController {

    @Autowired
    private SysTipsService tipsService;

    @PostMapping("getSysTipList")
    @ApiOperation(value = "站内信发送列表")
//    @AuthToken
    public ResponsePageList<SysTips> getBoomList(@RequestBody SearchModel<SysTips> searchModel){
        return ResponsePageList.success(tipsService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }
}
