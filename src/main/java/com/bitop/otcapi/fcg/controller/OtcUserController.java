package com.bitop.otcapi.fcg.controller;

import com.bitop.otcapi.fcg.entity.OtcUser;
import com.bitop.otcapi.fcg.entity.SearchModel;
import com.bitop.otcapi.fcg.entity.SysTips;
import com.bitop.otcapi.fcg.entity.req.JwtAuthenticationRequest;
import com.bitop.otcapi.fcg.service.OtcUserService;
import com.bitop.otcapi.fcg.service.SysTipsService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponsePageList;
import com.bitop.otcapi.util.MessageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @ApiOperation(value = "用户登录")
    @PostMapping("login")
//    @NoRepeatSubmit
    public Response<Map<String,String>> login(@RequestBody @Validated JwtAuthenticationRequest jwtAuthenticationRequest) {
        return Response.success(userService.login(jwtAuthenticationRequest)).message(MessageUtils.message("登录成功"));
    }

    /**
     * 用户信息包括（用户名 用户id 用户邀请码）
     */
//    @ApiOperation(value = "根据token获取用户信息")
//    @GetMapping("getMemberInfo")
//    public Response getEzUserInfo() {
//        userService.getById(getUserId());
//        return Response.success();
//    }
}
