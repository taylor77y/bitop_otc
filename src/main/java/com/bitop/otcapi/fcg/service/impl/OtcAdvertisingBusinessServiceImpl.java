package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.fcg.entity.OtcAdvertisingBusiness;
import com.bitop.otcapi.fcg.entity.req.OtcSettingReqDto;
import com.bitop.otcapi.fcg.mapper.OtcAdvertisingBusinessMapper;
import com.bitop.otcapi.fcg.service.OtcAdvertisingBusinessService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.util.EncoderUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OtcAdvertisingBusinessServiceImpl extends ServiceImpl<OtcAdvertisingBusinessMapper, OtcAdvertisingBusiness> implements OtcAdvertisingBusinessService {

    @Autowired
    private OtcAdvertisingBusinessMapper ezAdvertisingBusinessMapper;

    /***
     * @Description: 完善otc交易信息
     * @Param: [otcSettingReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2021/12/30
     */
    @Override
    @Transactional(value="transactionManager1", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response otcSetting(OtcSettingReqDto otcSettingReqDto) {
        String name = otcSettingReqDto.getAdvertisingName();
        String securityPassword = otcSettingReqDto.getSecurityPassword();
        String userId = ContextHandler.getUserId();
        //判断是否修改过
        LambdaQueryWrapper<OtcAdvertisingBusiness> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OtcAdvertisingBusiness::getUserId, userId);
        OtcAdvertisingBusiness advertisingBusiness = baseMapper.selectOne(queryWrapper);
        if (advertisingBusiness.getSecurityPassword() != null) {
            return Response.error("OTC信息不能进行修改");
        }
//        Stopwatch stopwatch = Stopwatch.createStarted();
        advertisingBusiness.setSecurityPassword(EncoderUtil.encode(securityPassword));
//        stopwatch.stop(); // optional
//        long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
//        log.info("time: " + stopwatch); // formatted string like "12.3 ms"

        Integer exist = ezAdvertisingBusinessMapper.existByAdvertisingName(name);

        if(!ObjectUtils.isEmpty(exist)){
            // 当存在时，执行这里的代码
            return Response.error("昵称重复，请重新输入");
        }
        else{
            // 当不存在时，执行这里的代码
        }
        advertisingBusiness.setAdvertisingName(name);
        baseMapper.updateById(advertisingBusiness);
        return Response.success();
    }


    @Override
    public void updateCount(String sellUserId, String buyUserId, Date payTime, Date finishTime, boolean isAdmin, String status) {
        List<OtcAdvertisingBusiness> businesses = new ArrayList<>();
        //根据用户查询到OTC详情
//        LambdaQueryWrapper<OtcAdvertisingBusiness> sell = new LambdaQueryWrapper<>();
//        sell.eq(OtcAdvertisingBusiness::getUserId, sellUserId);
//        OtcAdvertisingBusiness sellInfo = baseMapper.selectOne(sell);
        OtcAdvertisingBusiness sellInfo = ezAdvertisingBusinessMapper.selectOneByUserId(sellUserId);
        sellInfo.setSellCount(sellInfo.getSellCount() + 1);

        OtcAdvertisingBusiness buyInfo = ezAdvertisingBusinessMapper.selectOneByUserId(buyUserId);
        buyInfo.setBuyCount(buyInfo.getBuyCount() + 1);

        Integer totalSell = sellInfo.getSellCount() + sellInfo.getBuyCount() - 1;//卖家完成数量
        Double finishRateSell = sellInfo.getFinishRate();//完成率
        Double totalFillSell = (1 - finishRateSell) * totalSell;//未完成数量
        Double finishSell = totalSell - totalFillSell;//卖家成功完成数量

        Integer buyCount1 = sellInfo.getBuyCount();
        Double finishBuyRate1 = sellInfo.getFinishBuyRate();
        Double totalBuyFill1 = (1 - finishBuyRate1) * buyCount1;//未完成数量
        Double finishBuyRete1 = buyCount1 - totalBuyFill1;//完成数量+1


        Integer totalBuy = buyInfo.getSellCount() + buyInfo.getBuyCount() - 1;//买家完成数量
        Double finishRateBuy = buyInfo.getFinishRate();
        Double totalFillBuy = (1 - finishRateBuy) * totalBuy;//未完成数量
        Double finishBuy = totalBuy - totalFillBuy;//买家成功完成数量


        Integer buyCount = buyInfo.getBuyCount();
        Double finishBuyRate = buyInfo.getFinishBuyRate();
        Double totalBuyFill = (1 - finishBuyRate) * buyCount;//未完成数量
        Double finishBuyRete = buyCount - totalBuyFill;//完成数量+1

        if (isAdmin) {//修改完成率
            if ("0".equals(status)) {
                sellInfo.setFinishRate(finishSell / (totalSell + 1));//卖家降低完成率
                buyInfo.setFinishRate((finishBuy + 1) / (totalBuy + 1));//买家提升完成率
                buyInfo.setFinishBuyRate((finishBuyRete + 1) / (buyCount + 1));
            } else {
                sellInfo.setFinishRate((finishSell + 1) / (totalSell + 1));//增加降低完成率
                buyInfo.setFinishRate(finishBuy / (totalBuy + 1));//买家提升完成率
                buyInfo.setFinishBuyRate((finishBuyRete / buyCount) + 1);
            }
        } else {
            Long releaseTime = finishTime.getTime() - payTime.getTime();//放行时间
            var time = Math.floor(releaseTime / 60 % 60);
            buyInfo.setAveragePass((time + sellInfo.getAveragePass() * sellInfo.getSellCount()) / (finishSell + 1));//平均放行时间

            sellInfo.setFinishRate((finishSell + 1) / (totalSell + 1));//增加降低完成率
            buyInfo.setFinishRate((finishBuy + 1) / (totalBuy + 1));//买家提升完成率
            buyInfo.setFinishBuyRate((finishBuyRete + 1) / (buyCount + 1));
        }
        businesses.add(sellInfo);
        businesses.add(buyInfo);
        this.updateBatchById(businesses);
    }
}
