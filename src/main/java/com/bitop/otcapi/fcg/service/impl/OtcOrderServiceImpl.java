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
import java.time.LocalDateTime;
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
     * @Description: ?????? ??????????????????
     * @Param: [orderNo]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2022/01/02
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//value="transactionManager1",
    public Response releaseAdvertisingOrder(OtcOrderReqDto otcOrderReqDto) {
        String coinName = otcOrderReqDto.getCoinName();
        //?????? otc????????????????????????????????????
        String userId = otcOrderReqDto.getUserId();
        LambdaQueryWrapper<OtcAdvertisingBusiness> businessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        businessLambdaQueryWrapper.eq(OtcAdvertisingBusiness::getUserId, userId);
        OtcAdvertisingBusiness one = advertisingBusinessService.getOne(businessLambdaQueryWrapper);
        if (StringUtils.isEmpty(one.getSecurityPassword())) {
            return Response.error(MessageUtils.message("????????????otc????????????")).code(700);
        }
        LambdaQueryWrapper<CoinType> queryWrapper = new LambdaQueryWrapper<CoinType>();
        queryWrapper.eq(CoinType::getCoinName, coinName);
        CoinType coinType = coinTypeService.getOne(queryWrapper);//???????????????
        if (!coinTypeService.statusService(coinType, CoinConstant.OTC_STATUS)) {
            throw new BaseException("???????????????????????????");
        }
        BigDecimal amount = otcOrderReqDto.getTotalAmount();//????????????
        BigDecimal minimumLimit = otcOrderReqDto.getMinimumLimit();
        BigDecimal maximumLimit = otcOrderReqDto.getMaximumLimit();
        if (maximumLimit.compareTo(coinType.getMaxAmount()) > 0 || minimumLimit.compareTo(coinType.getMinAmount()) < 0) {
            throw new BaseException("????????????????????????");
        }
        OtcConfig otcConfig = configMapper.getOneById("1");//otc??????configService.getById(1)
        Integer prompt = otcOrderReqDto.getPrompt();
        if (prompt > otcConfig.getMaxPayTime() || prompt < otcConfig.getMinPayTime()) {
            throw new BaseException("?????????????????????????????????");
        }

        BigDecimal advertisingFeeRatio = coinType.getOtcFeeRatio();
        BigDecimal fee = advertisingFeeRatio.multiply(amount);//???????????????

        //???????????????
        List<BalanceChange> cList = new ArrayList<>();
        BalanceChange b = new BalanceChange();
        b.setCoinName(coinName);
        b.setUserId(userId);
        b.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
        b.setMainType(CoinConstants.MainType.FEE.getType());
        b.setSonType(RecordSonType.HANDLING_FEE);
        b.setAvailable(fee.negate());
        cList.add(b);

        //?????? otc?????? ??????
        OtcOrder ezOtcOrder = new OtcOrder();
        String orderNo = otcOrderIndexService.getOrderNoByCurrencyCode(otcOrderReqDto.getCurrencyCode(), IndexOrderNoKey.ORDER_INFO);
        ezOtcOrder.setOrderNo(orderNo);
        ezOtcOrder.setUserId(userId);
        if ("0".equals(otcOrderReqDto.getType())) {//???
            ezOtcOrder.setFrozeAmount(BigDecimal.ZERO);
        } else if ("1".equals(otcOrderReqDto.getType())) {//???
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

        //?????????????????????????????????????????????????????????
        paymentService.depositPayment(otcOrderReqDto.getPaymentMethod1(), otcOrderReqDto.getPaymentMethod2(), otcOrderReqDto.getPaymentMethod3(), userId, orderNo, null);

        if (!accountService.balanceChangeSYNC(cList)) {// ??????????????????
            throw new AccountOperationBusyException();
        }
        BeanUtils.copyProperties(otcOrderReqDto,ezOtcOrder);
        ezOtcOrder.setAdvertisingName(one.getAdvertisingName());
        ezOtcOrder.setStatus(0);
        //??????????????????
        baseMapper.insert(ezOtcOrder);
        //??????????????????????????????
        WebSocketHandle.nowOrder();
//        webSocketService.nowOrder();
        return Response.success();
    }


    /**
     * @param orderNo
     * @Description: ?????? ??????????????????
     * @Param: [orderNo]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2022/01/02
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//value="transactionManager1",
    public Response offShelfOrder(String orderNo) {
        //??????????????????????????????????????????????????????
//        LambdaQueryWrapper<OtcOrderMatch> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(OtcOrderMatch::getOrderNo, orderNo).and(we -> we.eq(
//                OtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT.getCode()).or()
//                .eq(OtcOrderMatch::getStatus, MatchOrderStatus.PAID.getCode()).or()
//                .eq(OtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER.getCode())
//                .eq(OtcOrderMatch::getStatus, MatchOrderStatus.APPEALING.getCode()));
//        List<OtcOrderMatch> list = orderMatchService.list(queryWrapper);
        List<OtcOrderMatch> list = orderMatchMapper.existUnfinishedOrderByNoAndStatus(orderNo,statusArray);
        if (!CollectionUtils.isEmpty(list)) {
            return Response.error(MessageUtils.message("????????????!????????????????????????,????????????"));
        }
        //??????????????????????????????
        OtcOrder ezOtcOrder = baseMapper.selectById(orderNo);
        //???????????????0????????? 1???????????????
        if ("1".equals(ezOtcOrder.getStatus())) {
            return Response.error("???????????????????????????");
        }
        if ("1".equals(ezOtcOrder.getType())) {// 1??????)
            BigDecimal frozeAmount = ezOtcOrder.getFrozeAmount();
            //TODO: ???????????????????????????????????????
            //???????????????USDT
            List<BalanceChange> cList = new ArrayList<>();
            BalanceChange b = new BalanceChange();
            b.setCoinName(ezOtcOrder.getCoinName());
            b.setAvailable(frozeAmount);//?????????????????????
            b.setFrozen(frozeAmount.negate());//?????????????????????
            b.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
            b.setUserId(ezOtcOrder.getUserId());
            b.setMainType(CoinConstants.MainType.UNFREEZE.getType());
            b.setSonType(RecordSonType.TRANSACTION_UNFREEZE);
            b.setFee(BigDecimal.ZERO);
            cList.add(b);
            if (!accountService.balanceChangeSYNC(cList)) {// ??????????????????
                throw new AccountOperationBusyException();
            }
        }
        //??????????????????
        ezOtcOrder.setStatus(1);
        ezOtcOrder.setEndTime(LocalDateTime.now());
        baseMapper.updateById(ezOtcOrder);
        return Response.success();
    }




    /**
     * @param placeOrderReqDto
     * @Description: ?????????????????????????????????/??????
     * @Param: [placeOrderReqDto]
     * @return: void
     * @Author: taylor
     * @Date: 2022/01/03
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//value="transactionManager1",
    public Response<PaymentDetailsRespDto> placeAnOrder(PlaceOrderReqDto placeOrderReqDto) {
        String userId = ContextHandler.getUserId();
        //?????????????????????????????????????????????
        String orderNo = placeOrderReqDto.getOrderNo();
        OtcOrder ezOtcOrder = baseMapper.selectById(orderNo);
        PaymentDetailsRespDto details = new PaymentDetailsRespDto();

        //???????????????????????????????????????
//        LambdaQueryWrapper<OtcOrderMatch> matchLambdaQueryWrapper = Wrappers.<OtcOrderMatch>lambdaQuery().
//                eq(OtcOrderMatch::getUserId, userId).eq(OtcOrderMatch::getType, ezOtcOrder.getType())
//                .and(wq -> wq.eq(OtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER.getCode())
//                        .or().eq(OtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT.getCode())
//                        .or().eq(OtcOrderMatch::getStatus, MatchOrderStatus.PAID.getCode())
//                        .or().eq(OtcOrderMatch::getStatus, MatchOrderStatus.APPEALING.getCode()));
//        OtcOrderMatch orderMatch = orderMatchService.getOne(matchLambdaQueryWrapper);//??????????????????????????????
        OtcOrderMatch orderMatch = orderMatchMapper.existUnfinishedOrderByTypeAndStatus(userId,ezOtcOrder.getType().toString(),statusArray);
        if (!ObjectUtils.isEmpty(orderMatch)) {
            BeanUtils.copyProperties(orderMatch, details);
            details.setNowTime(new Date());
            LambdaQueryWrapper<OtcOrderPayment> paymentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            //??????????????????
            if ("0".equals(orderMatch.getType())) {//??????
                paymentLambdaQueryWrapper.eq(OtcOrderPayment::getOrderMatchNo, orderMatch.getOrderMatchNo());
                details.setOtcOrderPayments(paymentService.list(paymentLambdaQueryWrapper));
            } else {//??????
                paymentLambdaQueryWrapper.eq(OtcOrderPayment::getOrderNo, orderMatch.getOrderNo());
                details.setOtcOrderPayments(paymentService.list(paymentLambdaQueryWrapper));
            }
            return Response.success(MessageUtils.message("????????????????????????????????????"), details);//???????????????
        }
        if ("1".equals(ezOtcOrder.getStatus())) {
            return Response.error(MessageUtils.message("???????????????????????????"));
        }
        LambdaQueryWrapper<CoinType> typeLambdaQueryWrapper = new LambdaQueryWrapper<CoinType>();
        typeLambdaQueryWrapper.eq(CoinType::getCoinName, ezOtcOrder.getCoinName());
        CoinType coinType = coinTypeService.getOne(typeLambdaQueryWrapper);//???????????????
        if (!coinTypeService.statusService(coinType, CoinConstant.OTC_STATUS)) {
            return Response.error(MessageUtils.message("????????????OTC???????????????"));
        }
        //??????otc????????????
        OtcConfig otcConfig = configMapper.getOneById("1");
        configService.checkOtcStatus(userId,otcConfig.getMaxCancelNum());

        //???????????????????????????????????????????????????
        String currencyCode = ezOtcOrder.getCurrencyCode();
        OtcUser user = userService.getById(userId);
        String countryCode = user.getCountryCode();
        //?????????????????????????????????
        LambdaQueryWrapper<OtcCountryConfig> configLambdaQueryWrapper = new LambdaQueryWrapper<>();
        configLambdaQueryWrapper.eq(OtcCountryConfig::getCountryCode, countryCode);
        OtcCountryConfig one = countryConfigService.getOne(configLambdaQueryWrapper);
        if (!one.getCurrencyCode().equals(currencyCode)) {
            return Response.error(MessageUtils.message("???????????????????????????????????????????????????????????????????????????"), 701);//???????????????
        }
        BigDecimal maximumLimit = ezOtcOrder.getMaximumLimit();//????????????
        BigDecimal minimumLimit = ezOtcOrder.getMinimumLimit();//????????????
        BigDecimal amount = placeOrderReqDto.getAmount();//????????????
        //????????????????????????
        if (amount.compareTo(maximumLimit) > 0 || amount.compareTo(minimumLimit) < 0) {
            return Response.error(MessageUtils.message("?????????????????????????????????"));
        }
        BigDecimal totalAmount = ezOtcOrder.getTotalAmount();//???????????????
        BigDecimal quotaAmount = ezOtcOrder.getQuotaAmount();//????????????
        BigDecimal nuQuotaAmount = totalAmount.subtract(quotaAmount);//??????????????????
        //??????????????????  ????????????????????????
        if (nuQuotaAmount.compareTo(amount) < 0) {
            return Response.error(MessageUtils.message("???????????????????????????"));
        }
        //OTC??????
        LambdaQueryWrapper<OtcAdvertisingBusiness> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OtcAdvertisingBusiness::getUserId, ezOtcOrder.getUserId()).or().eq(OtcAdvertisingBusiness::getUserId, userId);
        List<OtcAdvertisingBusiness> list = advertisingBusinessService.list(queryWrapper);
        Map<String, OtcAdvertisingBusiness> map = list.stream().collect(Collectors.toMap(OtcAdvertisingBusiness::getUserId, Function.identity()));
        //???????????????
        String orderMatchNo = orderIndexService.getOrderNoByCountryCode(countryCode, IndexOrderNoKey.ORDER_MATCH_INFO);
        //??????  ????????????(0:???  1??????)
        BigDecimal totalPrice = ezOtcOrder.getPrice().multiply(placeOrderReqDto.getAmount());//?????????

        OtcOrderMatch match = new OtcOrderMatch();//????????????
        BeanUtils.copyProperties(ezOtcOrder, match);
        details.setNowTime(new Date());
        match.setUserId(userId);
        match.setOtcOrderUserId(ezOtcOrder.getUserId());
        match.setOrderMatchNo(orderMatchNo);
        match.setOrderType(1);
        match.setAmount(placeOrderReqDto.getAmount());
        match.setTotalPrice(totalPrice);
        match.setAdvertisingName(map.get(ezOtcOrder.getUserId()).getAdvertisingName());
        match.setMatchAdvertisingName(map.get(userId).getAdvertisingName());
        BigDecimal feeRatio = coinType.getOtcFeeRatio();
        match.setFee(feeRatio.multiply(amount));

//        BeanUtils.copyBeanProp(details, ezOtcOrder);
        BeanUtils.copyProperties(ezOtcOrder, details);
        details.setAmount(placeOrderReqDto.getAmount());
        details.setOrderMatchNo(orderMatchNo);
        details.setOrderType("1");
        details.setTotalPrice(totalPrice);
        details.setAdvertisingName(map.get(ezOtcOrder.getUserId()).getAdvertisingName());

        //????????????????????????
        Integer paymentMethod1 = ezOtcOrder.getPaymentMethod1();
        Integer paymentMethod2 = ezOtcOrder.getPaymentMethod2();
        Integer paymentMethod3 = ezOtcOrder.getPaymentMethod3();
        if ("1".equals(ezOtcOrder.getType())) {//???????????????
            LambdaQueryWrapper<OtcOrderPayment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OtcOrderPayment::getOrderNo, ezOtcOrder.getOrderNo());
            //??????????????????????????????????????????
            details.setOtcOrderPayments(paymentService.list(lambdaQueryWrapper));
        } else {//????????????
            details.setOtcOrderPayments(paymentService.depositPayment(paymentMethod1, paymentMethod2, paymentMethod3, userId, null, orderMatchNo));
            //?????? ??????
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
            if (!accountService.balanceChangeSYNC(cList)) {// ??????????????????
                throw new AccountOperationBusyException();
            }
        }
        //?????????????????????????????????
        String isAdvertising = String.valueOf(ezOtcOrder.getIsAdvertising());
        Integer prompt = null;
        String sellUserId = null;
        String buyUserId = null;
        int flag = 0;
        if ("1".equals(isAdvertising)) {//?????????????????? ???????????????
            match.setStatus(MatchOrderStatus.WAITFORPAYMENT.getCode());
            details.setStatus(MatchOrderStatus.WAITFORPAYMENT.getCode());
            prompt = ezOtcOrder.getPrompt();
            if ("1".equals(ezOtcOrder.getType())) {//??????
                flag = 1;
                sellUserId = userId;
                buyUserId = match.getOtcOrderUserId();
            }
            if ("0".equals(ezOtcOrder.getType())) {//??????
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
        //????????????????????????
        ezOtcOrder.setQuotaAmount(quotaAmount.add(placeOrderReqDto.getAmount()));
        baseMapper.updateById(ezOtcOrder);//????????????

        Date beForeTime = DateUtils.getBeForeTime(prompt);
        details.setDueTime(beForeTime);
        match.setDueTime(LocalDateTime.now().minusMinutes(prompt));
        orderMatchService.save(match);
        //TODO:???????????????rabbitmq??????????????????  ??????????????????????????? ??????????????????????????????
        rabbitMQService.convert(orderMatchNo, match.getStatus(), prompt);
        //TODO:????????????
        if (flag == 1) {
            AsyncManager.me().execute(AsyncFactory.sendSysChat(sellUserId, buyUserId, orderMatchNo,
                    SysOrderConstants.SysChatMsg.BUY_PLACE_ORDER, MatchOrderStatus.WAITFORPAYMENT));
        } else if (flag == 2) {
            AsyncManager.me().execute(AsyncFactory.sendSysChat(sellUserId, buyUserId, orderMatchNo,
                    SysOrderConstants.SysChatMsg.SELL_PLACE_ORDER, MatchOrderStatus.WAITFORPAYMENT));
        }

        //?????????????????????
        WebSocketHandle.otherAuthentication(ezOtcOrder.getUserId(),ezOtcOrder.getType().toString(),
                placeOrderReqDto.getAmount()+ezOtcOrder.getCoinName());

        //????????????
        return Response.success(MessageUtils.message("????????????"), details);//???????????????
    }
}
