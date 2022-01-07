package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.constant.*;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.exception.AccountOperationBusyException;
import com.bitop.otcapi.exception.BaseException;
import com.bitop.otcapi.fcg.entity.*;
import com.bitop.otcapi.fcg.entity.req.OtcOrderReqDto;
import com.bitop.otcapi.fcg.entity.req.PlaceOrderReqDto;
import com.bitop.otcapi.fcg.entity.resp.PaymentDetailsRespDto;
import com.bitop.otcapi.fcg.entity.vo.BalanceChange;
import com.bitop.otcapi.fcg.mapper.OtcConfigMapper;
import com.bitop.otcapi.fcg.mapper.OtcOrderMapper;
import com.bitop.otcapi.fcg.mapper.OtcOrderMatchMapper;
import com.bitop.otcapi.fcg.service.*;
import com.bitop.otcapi.manager.AsyncManager;
import com.bitop.otcapi.manager.factory.AsyncFactory;
import com.bitop.otcapi.mq.service.RabbitMQService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.util.BeanUtils;
import com.bitop.otcapi.util.DateUtils;
import com.bitop.otcapi.util.MessageUtils;
import com.bitop.otcapi.websocket.WebSocketHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OtcOrderServiceImpl extends ServiceImpl<OtcOrderMapper, OtcOrder> implements OtcOrderService {

    @Autowired
    private OtcAdvertisingBusinessService advertisingBusinessService;

    @Autowired
    private CoinTypeService coinTypeService;

    @Autowired
    private CoinAccountService accountService;

    @Autowired
    private OtcConfigService configService;

    @Autowired
    private OtcConfigMapper configMapper;

    @Autowired
    private OtcUserService userService;

    @Autowired
    private OtcOrderIndexService otcOrderIndexService;

    @Autowired
    private OtcOrderPaymentService paymentService;

    @Autowired
    private RabbitMQService rabbitMQService;

    @Autowired
    private OtcCountryConfigService countryConfigService;

    @Autowired
    private OtcOrderIndexService orderIndexService;

    @Autowired
    private OtcOrderMatchService orderMatchService;

    @Autowired
    private OtcOrderMatchMapper orderMatchMapper;


    final static String[] statusArray = {MatchOrderStatus.PENDINGORDER.getCode(),MatchOrderStatus.WAITFORPAYMENT.getCode(),
            MatchOrderStatus.PAID.getCode(),MatchOrderStatus.APPEALING.getCode()};


    /**
     * @param otcOrderReqDto
     * @Description: 商户 发布广告订单
     * @Param: [orderNo]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2022/01/02
     */
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
        OtcConfig otcConfig = configMapper.getOneById("1");//otc配置configService.getById(1)
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

        if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
            throw new AccountOperationBusyException();
        }
        BeanUtils.copyProperties(otcOrderReqDto,ezOtcOrder);
        ezOtcOrder.setAdvertisingName(one.getAdvertisingName());
        //存入新的订单
        baseMapper.insert(ezOtcOrder);
        //给用户一个新订单信号
        WebSocketHandle.nowOrder();
//        webSocketService.nowOrder();
        return Response.success();
    }


    /**
     * @param orderNo
     * @Description: 商户 下架广告订单
     * @Param: [orderNo]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2022/01/02
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//value="transactionManager1",
    public Response offShelfOrder(String orderNo) {
        //根据订单号查询到是否存在未完成的订单
//        LambdaQueryWrapper<OtcOrderMatch> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(OtcOrderMatch::getOrderNo, orderNo).and(we -> we.eq(
//                OtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT.getCode()).or()
//                .eq(OtcOrderMatch::getStatus, MatchOrderStatus.PAID.getCode()).or()
//                .eq(OtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER.getCode())
//                .eq(OtcOrderMatch::getStatus, MatchOrderStatus.APPEALING.getCode()));
//        List<OtcOrderMatch> list = orderMatchService.list(queryWrapper);
        List<OtcOrderMatch> list = orderMatchMapper.existUnfinishedOrderByNoAndStatus(orderNo,statusArray);
        if (!CollectionUtils.isEmpty(list)) {
            return Response.error(MessageUtils.message("下架失败!有用户订单未完成,请先处理"));
        }
        //根据订单号查询到订单
        OtcOrder ezOtcOrder = baseMapper.selectById(orderNo);
        //订单状态（0：正常 1：已下架）
        if ("1".equals(ezOtcOrder.getStatus())) {
            return Response.error("订单状态已发生变化");
        }
        if ("1".equals(ezOtcOrder.getType())) {// 1：卖)
            BigDecimal frozeAmount = ezOtcOrder.getFrozeAmount();
            //TODO: 将冻结数量返回商户的资产中
            //解冻卖出的USDT
            List<BalanceChange> cList = new ArrayList<>();
            BalanceChange b = new BalanceChange();
            b.setCoinName(ezOtcOrder.getCoinName());
            b.setAvailable(frozeAmount);//返回冻结的数量
            b.setFrozen(frozeAmount.negate());//解冻冻结的数量
            b.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
            b.setUserId(ezOtcOrder.getUserId());
            b.setMainType(CoinConstants.MainType.UNFREEZE.getType());
            b.setSonType(RecordSonType.TRANSACTION_UNFREEZE);
            b.setFee(BigDecimal.ZERO);
            cList.add(b);
            if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                throw new AccountOperationBusyException();
            }
        }
        //改变订单状态
        ezOtcOrder.setStatus("1");
        ezOtcOrder.setEndTime(new Date());
        baseMapper.updateById(ezOtcOrder);
        return Response.success();
    }




    /**
     * @param placeOrderReqDto
     * @Description: 用户根据订单号下单购买/出售
     * @Param: [placeOrderReqDto]
     * @return: void
     * @Author: taylor
     * @Date: 2022/01/03
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//value="transactionManager1",
    public Response<PaymentDetailsRespDto> placeAnOrder(PlaceOrderReqDto placeOrderReqDto) {
        String userId = ContextHandler.getUserId();
        //通过订单号查询到购买的订单
        String orderNo = placeOrderReqDto.getOrderNo();
        OtcOrder ezOtcOrder = baseMapper.selectById(orderNo);
        PaymentDetailsRespDto details = new PaymentDetailsRespDto();

        //查看用户是否有未完成的订单
//        LambdaQueryWrapper<OtcOrderMatch> matchLambdaQueryWrapper = Wrappers.<OtcOrderMatch>lambdaQuery().
//                eq(OtcOrderMatch::getUserId, userId).eq(OtcOrderMatch::getType, ezOtcOrder.getType())
//                .and(wq -> wq.eq(OtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER.getCode())
//                        .or().eq(OtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT.getCode())
//                        .or().eq(OtcOrderMatch::getStatus, MatchOrderStatus.PAID.getCode())
//                        .or().eq(OtcOrderMatch::getStatus, MatchOrderStatus.APPEALING.getCode()));
//        OtcOrderMatch orderMatch = orderMatchService.getOne(matchLambdaQueryWrapper);//匹配订单是否有未完成
        OtcOrderMatch orderMatch = orderMatchMapper.existUnfinishedOrderByTypeAndStatus(userId,ezOtcOrder.getType(),statusArray);
        if (!ObjectUtils.isEmpty(orderMatch)) {
            BeanUtils.copyProperties(orderMatch, details);
            details.setNowTime(new Date());
            LambdaQueryWrapper<OtcOrderPayment> paymentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            //判断订单类型
            if ("0".equals(orderMatch.getType())) {//买单
                paymentLambdaQueryWrapper.eq(OtcOrderPayment::getOrderMatchNo, orderMatch.getOrderMatchNo());
                details.setOtcOrderPayments(paymentService.list(paymentLambdaQueryWrapper));
            } else {//卖单
                paymentLambdaQueryWrapper.eq(OtcOrderPayment::getOrderNo, orderMatch.getOrderNo());
                details.setOtcOrderPayments(paymentService.list(paymentLambdaQueryWrapper));
            }
            return Response.success(MessageUtils.message("请先完成当前未完成的订单"), details);//将订单返回
        }
        if ("1".equals(ezOtcOrder.getStatus())) {
            return Response.error(MessageUtils.message("订单状态已发生变化"));
        }
        LambdaQueryWrapper<CoinType> typeLambdaQueryWrapper = new LambdaQueryWrapper<CoinType>();
        typeLambdaQueryWrapper.eq(CoinType::getCoinName, ezOtcOrder.getCoinName());
        CoinType coinType = coinTypeService.getOne(typeLambdaQueryWrapper);//查询到币种
        if (!coinTypeService.statusService(coinType, CoinConstant.OTC_STATUS)) {
            return Response.error(MessageUtils.message("当前币种OTC交易尚未开"));
        }
        //获取otc基本配置
        OtcConfig otcConfig = configMapper.getOneById("1");
        configService.checkOtcStatus(userId,otcConfig.getMaxCancelNum());

        //判断用户注册的国籍是否满足购买条件
        String currencyCode = ezOtcOrder.getCurrencyCode();
        OtcUser user = userService.getById(userId);
        String countryCode = user.getCountryCode();
        //通过国家编号查询到国家
        LambdaQueryWrapper<OtcCountryConfig> configLambdaQueryWrapper = new LambdaQueryWrapper<>();
        configLambdaQueryWrapper.eq(OtcCountryConfig::getCountryCode, countryCode);
        OtcCountryConfig one = countryConfigService.getOne(configLambdaQueryWrapper);
        if (!one.getCurrencyCode().equals(currencyCode)) {
            return Response.error(MessageUtils.message("根据您注册所在地的相关规定，您只能交易本地区的法币"), 701);//将订单返回
        }
        BigDecimal maximumLimit = ezOtcOrder.getMaximumLimit();//最大限额
        BigDecimal minimumLimit = ezOtcOrder.getMinimumLimit();//最小限额
        BigDecimal amount = placeOrderReqDto.getAmount();//购买数量
        //判断数量是否满足
        if (amount.compareTo(maximumLimit) > 0 || amount.compareTo(minimumLimit) < 0) {
            return Response.error(MessageUtils.message("输入数量不满足条件范围"));
        }
        BigDecimal totalAmount = ezOtcOrder.getTotalAmount();//广告总数量
        BigDecimal quotaAmount = ezOtcOrder.getQuotaAmount();//匹配数量
        BigDecimal nuQuotaAmount = totalAmount.subtract(quotaAmount);//未匹配的数量
        //未匹配的数量  是否大于购买数量
        if (nuQuotaAmount.compareTo(amount) < 0) {
            return Response.error(MessageUtils.message("订单数量已发生改变"));
        }
        //OTC信息
        LambdaQueryWrapper<OtcAdvertisingBusiness> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OtcAdvertisingBusiness::getUserId, ezOtcOrder.getUserId()).or().eq(OtcAdvertisingBusiness::getUserId, userId);
        List<OtcAdvertisingBusiness> list = advertisingBusinessService.list(queryWrapper);
        Map<String, OtcAdvertisingBusiness> map = list.stream().collect(Collectors.toMap(OtcAdvertisingBusiness::getUserId, Function.identity()));
        //得到订单号
        String orderMatchNo = orderIndexService.getOrderNoByCountryCode(countryCode, IndexOrderNoKey.ORDER_MATCH_INFO);
        //查看  订单类型(0:买  1：卖)
        BigDecimal totalPrice = ezOtcOrder.getPrice().multiply(placeOrderReqDto.getAmount());//总价格

        OtcOrderMatch match = new OtcOrderMatch();//封装订单
        BeanUtils.copyProperties(ezOtcOrder, match);
        details.setNowTime(new Date());
        match.setUserId(userId);
        match.setOtcOrderUserId(ezOtcOrder.getUserId());
        match.setOrderMatchNo(orderMatchNo);
        match.setAmount(placeOrderReqDto.getAmount());
        match.setTotalPrice(totalPrice);
        match.setAdvertisingName(map.get(ezOtcOrder.getUserId()).getAdvertisingName());
        match.setMatchAdvertisingName(map.get(userId).getAdvertisingName());

//        BeanUtils.copyBeanProp(details, ezOtcOrder);
        BeanUtils.copyProperties(ezOtcOrder, details);
        details.setAmount(placeOrderReqDto.getAmount());
        details.setOrderMatchNo(orderMatchNo);
        details.setOrderType("1");
        details.setTotalPrice(totalPrice);
        details.setAdvertisingName(map.get(ezOtcOrder.getUserId()).getAdvertisingName());

        //根据支付方式查询
        Integer paymentMethod1 = ezOtcOrder.getPaymentMethod1();
        Integer paymentMethod2 = ezOtcOrder.getPaymentMethod2();
        Integer paymentMethod3 = ezOtcOrder.getPaymentMethod3();
        if ("1".equals(ezOtcOrder.getType())) {//卖单的时候
            LambdaQueryWrapper<OtcOrderPayment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OtcOrderPayment::getOrderNo, ezOtcOrder.getOrderNo());
            //根据发布订单号查询到支付详情
            details.setOtcOrderPayments(paymentService.list(lambdaQueryWrapper));
        } else {//买的时候
            details.setOtcOrderPayments(paymentService.depositPayment(paymentMethod1, paymentMethod2, paymentMethod3, userId, null, orderMatchNo));
            //冻结 费用
            List<BalanceChange> cList = new ArrayList<>();
            BalanceChange b = new BalanceChange();
            b.setCoinName(ezOtcOrder.getCoinName());
            b.setAvailable(placeOrderReqDto.getAmount().negate());
            b.setFrozen(placeOrderReqDto.getAmount());
            b.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            b.setMainType(CoinConstants.MainType.FROZEN.getType());
            b.setUserId(userId);
            b.setSonType(RecordSonType.TRANSACTION_FREEZE);
            cList.add(b);
            if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                throw new AccountOperationBusyException();
            }
        }
        //查看订单是否为接单广告
        String isAdvertising = ezOtcOrder.getIsAdvertising();
        Integer prompt = null;
        String sellUserId = null;
        String buyUserId = null;
        int flag = 0;
        if ("1".equals(isAdvertising)) {//不为接单广告 或者为买单
            match.setStatus(MatchOrderStatus.WAITFORPAYMENT.getCode());
            details.setStatus(MatchOrderStatus.WAITFORPAYMENT.getCode());
            prompt = ezOtcOrder.getPrompt();
            if ("1".equals(ezOtcOrder.getType())) {//卖单
                flag = 1;
                sellUserId = userId;
                buyUserId = match.getOtcOrderUserId();
            }
            if ("0".equals(ezOtcOrder.getType())) {//买单
                flag = 2;
                sellUserId = userId;
                buyUserId = match.getOtcOrderUserId();
            }
        } else {
            flag = 3;
            sellUserId = userId;
            buyUserId = match.getOtcOrderUserId();
            match.setStatus(MatchOrderStatus.PENDINGORDER.getCode());
            details.setStatus(MatchOrderStatus.PENDINGORDER.getCode());
            prompt = otcConfig.getOrderTime();
        }
        //增加商家匹配数量
        ezOtcOrder.setQuotaAmount(quotaAmount.add(placeOrderReqDto.getAmount()));
        baseMapper.updateById(ezOtcOrder);//修改订单

        Date beForeTime = DateUtils.getBeForeTime(prompt);
        details.setDueTime(beForeTime);
        match.setDueTime(beForeTime);
        orderMatchService.save(match);
        //TODO:将订单存入rabbitmq进行死信通信  时间到了就取消订单 根据卖家用户设置而定
        rabbitMQService.convert(orderMatchNo, match.getStatus(), prompt);
        //TODO:存入消息
        if (flag == 1) {
            AsyncManager.me().execute(AsyncFactory.sendSysChat(sellUserId, buyUserId, orderMatchNo,
                    SysOrderConstants.SysChatMsg.BUY_PLACE_ORDER, MatchOrderStatus.WAITFORPAYMENT));
        } else if (flag == 2) {
            AsyncManager.me().execute(AsyncFactory.sendSysChat(sellUserId, buyUserId, orderMatchNo,
                    SysOrderConstants.SysChatMsg.SELL_PLACE_ORDER, MatchOrderStatus.WAITFORPAYMENT));
        }

        //给用户一个信号
        WebSocketHandle.otherAuthentication(ezOtcOrder.getUserId(),ezOtcOrder.getType(),
                placeOrderReqDto.getAmount()+ezOtcOrder.getCoinName());

        //返回订单
        return Response.success(MessageUtils.message("下单成功"), details);//将订单返回
    }
}
