package com.bitop.otcapi.fcg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bitop.otcapi.constant.MatchOrderStatus;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.fcg.entity.OtcAdvertisingBusiness;
import com.bitop.otcapi.fcg.entity.OtcOrderMatch;
import com.bitop.otcapi.fcg.entity.OtcUser;
import com.bitop.otcapi.fcg.entity.SearchModel;
import com.bitop.otcapi.fcg.entity.req.BusinessReqDto;
import com.bitop.otcapi.fcg.entity.resp.MerchantsBussinessStatsRespDto;
import com.bitop.otcapi.fcg.service.OtcAdvertisingBusinessService;
import com.bitop.otcapi.fcg.service.OtcOrderMatchService;
import com.bitop.otcapi.fcg.service.OtcUserService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponsePageList;
import com.bitop.otcapi.util.BeanUtils;
import com.bitop.otcapi.util.DateUtils;
import com.bitop.otcapi.util.EncoderUtil;
import com.bitop.otcapi.util.MessageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@Api(tags = "OTC-广告商户模块")
@RequestMapping("/otc/advertisingbusiness")
public class OtcAdvertisingBusinessController {

    @Autowired
    private OtcAdvertisingBusinessService advertisingBusinessService;

    @Autowired
    private OtcOrderMatchService orderMatchService;

    @Autowired
    private OtcUserService userService;

    @ApiOperation(value = "OTC广告商户列表")
    @PostMapping("advertisingBusinessList")
//    @AuthToken
    public ResponsePageList<OtcAdvertisingBusiness> advertisingBusinessList(@RequestBody SearchModel<OtcAdvertisingBusiness> searchModel) {
        return ResponsePageList.success(advertisingBusinessService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @ApiOperation(value = "修改OTC广告商户信息")
    @PostMapping("updateAdvertisingBusiness")
//    @AuthToken
//    @Log(title = "OTC模块", logInfo ="修改商户信息", operatorType = OperatorType.MANAGE)
    public Response updateAdvertisingBusiness(@RequestBody BusinessReqDto businessReqDto){
        OtcAdvertisingBusiness advertisingBusiness = new OtcAdvertisingBusiness();
        BeanUtils.copyProperties(businessReqDto, advertisingBusiness);
        if (StringUtils.hasLength(businessReqDto.getSecurityPassword())){
            advertisingBusiness.setSecurityPassword(EncoderUtil.encode(businessReqDto.getSecurityPassword()));
        }
        advertisingBusiness.setUpdateBy(ContextHandler.getUserName());
        advertisingBusinessService.updateById(advertisingBusiness);
        return Response.success();

    }


    //OTC交易word文档，新增[OTC]，展示用戶使用OTC方式信息
    @ApiOperation(value = "OTC商家交易汇总信息")
    @ApiImplicitParam(name = "userId",value = "userId",required = false)
    @PostMapping({"advertisingBusiness/{userId}", "advertisingBusiness"})
//    @AuthToken
    public Response<MerchantsBussinessStatsRespDto> getMonthlyMerchantsStatistics(@PathVariable(value = "userId", required = false) String userId) {
        String userId2 = ContextHandler.getUserId();
        String userId1 = StringUtils.hasLength(userId) ? userId : userId2;
        LambdaQueryWrapper<OtcAdvertisingBusiness> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OtcAdvertisingBusiness::getUserId, userId1);
        OtcAdvertisingBusiness one = advertisingBusinessService.getOne(lambdaQueryWrapper);
        if (StringUtils.isEmpty(userId) && StringUtils.isEmpty(one.getSecurityPassword())) {
            return Response.error(MessageUtils.message("请先完善otc交易信息"),700);
        }
        OtcUser user = userService.getById(userId1);
        String kycStatus = user.getKycStatus().toString();

        String level = user.getLevel().toString();
        MerchantsBussinessStatsRespDto merchantsBussinessStatsRespDto = new MerchantsBussinessStatsRespDto();
        BeanUtils.copyProperties(one, merchantsBussinessStatsRespDto);
        merchantsBussinessStatsRespDto.setKycStatus(kycStatus);
        merchantsBussinessStatsRespDto.setAdvertisingStatus(level);
        merchantsBussinessStatsRespDto.setRegistrationTime(one.getCreateTime());
        merchantsBussinessStatsRespDto.setMargin(one.getMargin());
        //月成功成交笔数（以三十日为基准计算）
        LambdaQueryWrapper<OtcOrderMatch> q = new LambdaQueryWrapper<OtcOrderMatch>();
        q.eq(OtcOrderMatch::getOtcOrderUserId, userId1);
        q.eq(OtcOrderMatch::getStatus, MatchOrderStatus.COMPLETED.getCode());
        Date ndayStart = DateUtils.getNdayStart(-30);
        q.gt(OtcOrderMatch::getFinishTime, ndayStart);
        merchantsBussinessStatsRespDto.setMonthlySuccessCount(orderMatchService.count(q));


        //月发起笔数（以三十日为基准计算）
        q = new LambdaQueryWrapper<OtcOrderMatch>();
        q.eq(OtcOrderMatch::getOtcOrderUserId, userId1);
        ndayStart = DateUtils.getNdayStart(-30);
        q.gt(OtcOrderMatch::getFinishTime, ndayStart);
        merchantsBussinessStatsRespDto.setMonthlySuccessPercent(merchantsBussinessStatsRespDto.getMonthlySuccessCount()/orderMatchService.count(q));


        //总成交笔数
        q = new LambdaQueryWrapper<OtcOrderMatch>();
        q.eq(OtcOrderMatch::getOtcOrderUserId, userId1);
        q.eq(OtcOrderMatch::getStatus, MatchOrderStatus.COMPLETED.getCode());
        merchantsBussinessStatsRespDto.setGrandTotal(orderMatchService.count(q));

        //总买入资产（计算买入的数字货币）
        //由于 LambdaQueryWrapper 并不支持使用sum等求和的聚合函数
        OtcOrderMatch ezOtcOrderMatch = new OtcOrderMatch();
        QueryWrapper<OtcOrderMatch> qw = new QueryWrapper<OtcOrderMatch>();
        qw.select("IFNULL(sum(price),0) as totalPrice").eq("type", "0")
                .eq("otc_order_user_id", userId1).eq("status", MatchOrderStatus.COMPLETED);
        ezOtcOrderMatch = orderMatchService.getOne(qw);
        merchantsBussinessStatsRespDto.setTotalpurchasedAssets(ezOtcOrderMatch.getTotalPrice());


        //总卖出资产（计算卖出的数字货币）
        ezOtcOrderMatch = new OtcOrderMatch();
        qw = new QueryWrapper<OtcOrderMatch>();
        qw.select("IFNULL(sum(price),0) as totalPrice").eq("type", "1")
                .eq("otc_order_user_id", userId1).eq("status", MatchOrderStatus.COMPLETED);
        ezOtcOrderMatch = orderMatchService.getOne(qw);
        merchantsBussinessStatsRespDto.setTotalSoldAssets(ezOtcOrderMatch.getTotalPrice());


        return Response.success(merchantsBussinessStatsRespDto);
    }
}
