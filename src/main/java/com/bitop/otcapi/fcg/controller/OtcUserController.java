package com.bitop.otcapi.fcg.controller;

import com.bitop.otcapi.fcg.entity.OtcUser;
import com.bitop.otcapi.fcg.entity.SearchModel;
import com.bitop.otcapi.fcg.entity.SysTips;
import com.bitop.otcapi.fcg.service.OtcUserService;
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
@Api(tags = "系统模块-用户中心")
@RequestMapping("/system/user")
public class OtcUserController {

    @Autowired
    private OtcUserService userService;

    @PostMapping("userList")
    @ApiOperation(value = "用户列表")
//    @AuthToken
    public ResponsePageList<OtcUser> getBoomList(@RequestBody SearchModel<OtcUser> searchModel){
        return ResponsePageList.success(userService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }
}
