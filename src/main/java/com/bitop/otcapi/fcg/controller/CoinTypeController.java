package com.bitop.otcapi.fcg.controller;

import com.bitop.otcapi.fcg.entity.CoinType;
import com.bitop.otcapi.fcg.entity.SearchModel;
import com.bitop.otcapi.fcg.service.CoinTypeService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponseList;
import com.bitop.otcapi.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 币种类型表 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@RestController
@Api(tags = "OTC-币种模块")
@RequestMapping("/otc/coin/type")
public class CoinTypeController {

    @Autowired
    private CoinTypeService otcCoinTypeService;

    @ApiOperation(value = "币种列表")
    @PostMapping("/coinTypeList")
//    @AuthToken
    public ResponsePageList<CoinType> coinTypeList(@RequestBody @Valid SearchModel<CoinType> searchModel){
        return ResponsePageList.success(otcCoinTypeService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

//    @NoRepeatSubmit
    @ApiOperation(value = "修改币种配置")
    @PostMapping("updateCoinTypeConfig")
//    @AuthToken
//    @Log(title = "资产币种模块", logInfo ="币种模块", operatorType = OperatorType.MANAGE)
    public Response updateOtcConfig(@RequestBody @Valid CoinType type) {
        otcCoinTypeService.updateById(type);
        return Response.success();
    }

    @ApiOperation(value = "修改币种状态")
    @PostMapping("updateCoinTypeStatusById")
    @ApiImplicitParam(name = "id",value = "id",required = true)
//    @AuthToken
//    @Log(title = "资产币种模块", logInfo ="币种模块", operatorType = OperatorType.MANAGE)
    public Response updateCoinTypeStatusById(@PathVariable String id,@PathVariable String status) {
        otcCoinTypeService.updateCoinTypeStatusById(id, status);
        return Response.success();
    }

    @ApiOperation(value = "查询所有可用 coin 信息")
//    @AuthToken
    @GetMapping("queryAllCoins")// 交易类型：在线购买、卖出
    public ResponseList<CoinType> queryAllCoins(){

        return ResponseList.success(otcCoinTypeService.queryAllCoinsFromHuobi());
    }

    @ApiOperation(value = "法币币种（抓取系统参数配置的法币）")
//    @AuthToken
    @GetMapping("fiatList")
    public ResponseList<CoinType> fiatList(){
        return ResponseList.success(otcCoinTypeService.fiatListFromBinance());
    }
}
