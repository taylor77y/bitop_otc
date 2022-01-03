package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.constant.CoinConstant;
import com.bitop.otcapi.constant.CoinConstants;
import com.bitop.otcapi.constant.IndexOrderNoKey;
import com.bitop.otcapi.constant.RecordSonType;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.exception.AccountOperationBusyException;
import com.bitop.otcapi.exception.BaseException;
import com.bitop.otcapi.fcg.entity.*;
import com.bitop.otcapi.fcg.entity.req.NewOrderRespDto;
import com.bitop.otcapi.fcg.entity.req.OtcOrderReqDto;
import com.bitop.otcapi.fcg.entity.vo.BalanceChange;
import com.bitop.otcapi.fcg.mapper.OtcOrderMapper;
import com.bitop.otcapi.fcg.service.*;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.util.BeanUtils;
import com.bitop.otcapi.util.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OtcOrderServiceImpl extends ServiceImpl<OtcOrderMapper, OtcOrder> implements OtcOrderService {

    @Autowired
    private OtcAdvertisingBusinessService advertisingBusinessService;

    @Autowired
    private CoinTypeService coinTypeService;

    @Autowired
    private OtcConfigService otcConfigService;

    @Autowired
    private OtcOrderIndexService otcOrderIndexService;

    @Autowired
    private WebSocketService webSocketService;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//value="transactionManager1",
    public Response releaseAdvertisingOrder(OtcOrderReqDto otcOrderReqDto) {
        String coinName = otcOrderReqDto.getCoinName();
        //查看 otc广告商户信息是否有过修改
        String userId = otcOrderReqDto.getUserId();
        LambdaQueryWrapper<OtcAdvertisingBusiness> businessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        businessLambdaQueryWrapper.eq(OtcAdvertisingBusiness::getUserId, userId);
        OtcAdvertisingBusiness one = advertisingBusinessService.getOne(businessLambdaQueryWrapper);
        if (StringUtils.isEmpty(one.getSecurityPassword())) {
            return Response.error(MessageUtils.message("请先完善otc交易信息")).code(700);
        }
        LambdaQueryWrapper<CoinType> queryWrapper = new LambdaQueryWrapper<CoinType>();
        queryWrapper.eq(CoinType::getCoinName, coinName);
        CoinType coinType = coinTypeService.getOne(queryWrapper);//查询到币种
        if (!coinTypeService.statusService(coinType, CoinConstant.OTC_STATUS)) {
            throw new BaseException("此币种尚未开放交易");
        }
        BigDecimal amount = otcOrderReqDto.getTotalAmount();//发布数量
        BigDecimal minimumLimit = otcOrderReqDto.getMinimumLimit();
        BigDecimal maximumLimit = otcOrderReqDto.getMaximumLimit();
        if (maximumLimit.compareTo(coinType.getMaxAmount()) > 0 || minimumLimit.compareTo(coinType.getMinAmount()) < 0) {
            throw new BaseException("发布数量不在限额");
        }
        OtcConfig otcConfig = otcConfigService.getById(1);//otc配置
        Integer prompt = otcOrderReqDto.getPrompt();
        if (prompt > otcConfig.getMaxPayTime() || prompt < otcConfig.getMinPayTime()) {
            throw new BaseException("付款时间不在限定时间内");
        }

        BigDecimal advertisingFeeRatio = coinType.getOtcFeeRatio();
        BigDecimal fee = advertisingFeeRatio.multiply(amount);//比例手续费

        //扣除手续费
        List<BalanceChange> cList = new ArrayList<>();
        BalanceChange b = new BalanceChange();
        b.setCoinName(coinName);
        b.setUserId(userId);
        b.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
        b.setMainType(CoinConstants.MainType.FREE.getType());
        b.setSonType(RecordSonType.HANDLING_FEE);
        b.setAvailable(fee.negate());
        cList.add(b);

        //判断 otc订单 买卖
        OtcOrder ezOtcOrder = new OtcOrder();
        String orderNo = otcOrderIndexService.getOrderNoByCurrencyCode(otcOrderReqDto.getCurrencyCode(), IndexOrderNoKey.ORDER_INFO);
        ezOtcOrder.setOrderNo(orderNo);
        ezOtcOrder.setUserId(userId);
        if ("0".equals(otcOrderReqDto.getType())) {//买
            ezOtcOrder.setFrozeAmount(BigDecimal.ZERO);
        } else if ("1".equals(otcOrderReqDto.getType())) {//卖
            ezOtcOrder.setFrozeAmount(amount);
            BalanceChange b2 = new BalanceChange();
            b2.setCoinName(coinName);
            b2.setUserId(userId);
            b2.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            b2.setMainType(CoinConstants.MainType.FROZEN.getType());
            b2.setSonType(RecordSonType.TRANSACTION_FREEZE);
            b2.setFrozen(amount);
            b2.setAvailable(amount.negate());
            cList.add(b2);
        }

//        if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
//            throw new AccountOperationBusyException();
//        }
        BeanUtils.copyProperties(otcOrderReqDto,ezOtcOrder);
        ezOtcOrder.setAdvertisingName(one.getAdvertisingName());
        //存入新的订单
        baseMapper.insert(ezOtcOrder);
        //给用户一个新订单信号
        webSocketService.nowOrder();
        return Response.success();
    }

}
