package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.constant.*;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.exception.AccountOperationBusyException;
import com.bitop.otcapi.exception.BaseException;
import com.bitop.otcapi.fcg.entity.CoinRecord;
import com.bitop.otcapi.fcg.entity.OtcOrder;
import com.bitop.otcapi.fcg.entity.OtcOrderMatch;
import com.bitop.otcapi.fcg.entity.OtcOrderPayment;
import com.bitop.otcapi.fcg.entity.req.AdMatchOrderQueryReqDto;
import com.bitop.otcapi.fcg.entity.resp.OrderRecordRespDto;
import com.bitop.otcapi.fcg.entity.vo.BalanceChange;
import com.bitop.otcapi.fcg.mapper.OtcOrderMatchMapper;
import com.bitop.otcapi.fcg.service.*;
import com.bitop.otcapi.manager.AsyncManager;
import com.bitop.otcapi.manager.factory.AsyncFactory;
import com.bitop.otcapi.redis.RedisCache;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponseList;
import com.bitop.otcapi.util.BeanUtils;
import com.bitop.otcapi.util.DateUtils;
import com.bitop.otcapi.util.MessageUtils;
import com.bitop.otcapi.websocket.WebSocketHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OtcOrderMatchServiceImpl extends ServiceImpl<OtcOrderMatchMapper, OtcOrderMatch> implements OtcOrderMatchService {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private OtcOrderService otcOrderService;

    @Autowired
    private CoinAccountService accountService;

    @Autowired
    private CoinRecordService recordService;

    @Autowired
    private OtcUserService userService;

    @Autowired
    private OtcOrderPaymentService orderPaymentService;

    /***
     * @Description: 用户 取消订单（两个状态可取消订单  1：接单广告（卖家未接受订单）用户免费取消
     *                                              2：接单广告/普通广告（用户未支付状态） 用户取消次数增加）
     * @Param: [matchOrderNo]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2022/01/04
     * @param matchOrderNo
     */
    @Override
    public Response cancelOrder(String matchOrderNo) {
        String userId = ContextHandler.getUserId();
        //根据订单号查询到订单
        OtcOrderMatch orderMatch = baseMapper.selectById(matchOrderNo);
        String orderNo = orderMatch.getOrderNo();
        //查询到上架订单
        OtcOrder otcOrder = otcOrderService.getById(orderNo);
        List<BalanceChange> cList = new ArrayList<>();
        BalanceChange b = new BalanceChange();
        b.setCoinName(orderMatch.getCoinName());
        b.setAvailable(orderMatch.getAmount());
        b.setFrozen(orderMatch.getAmount().negate());
        b.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
        b.setMainType(CoinConstants.MainType.UNFREEZE.getType());
        b.setSonType(RecordSonType.TRANSACTION_UNFREEZE);
        //查看订单状态
        String sellUserId = null;
        String buyUserId = null;
        if (orderMatch.getStatus().equals(MatchOrderStatus.WAITFORPAYMENT.getCode())) {
            orderMatch.setStatus(MatchOrderStatus.CANCELLED.getCode());
            //将订单匹配数量增加回去
            otcOrder.setQuotaAmount(otcOrder.getQuotaAmount().subtract(orderMatch.getAmount()));
            //判断是谁取消的订单
            if (otcOrder.getUserId().equals(userId)) {//商户取消的订单
                sellUserId = userId;
                buyUserId = orderMatch.getUserId();
                if ("1".equals(otcOrder.getType())) {//卖单
                    //退后用户冻结的订单
                }
                if ("0".equals(otcOrder.getType())) {//买单
                    //解冻金额
                    b.setUserId(orderMatch.getUserId());
                    cList.add(b);
                    if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                        throw new AccountOperationBusyException();
                    }
                }
            } else {//用户取消的订单
                sellUserId = userId;
                buyUserId = otcOrder.getUserId();
                if ("1".equals(otcOrder.getType())) {//卖单
                    //退后用户冻结的订单
                }
                if ("0".equals(otcOrder.getType())) {//买单
                    //解冻金额
                    b.setUserId(userId);
                    if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                        throw new AccountOperationBusyException();
                    }
                }
                //查询当前用户取消订单数量
                int count = 1;
                Object object = redisCache.getCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + userId);
                if (null != object) {
                    count = (Integer) object;
                    count += 1; //用户取消次数增加
                }
                redisCache.setCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + userId, count, Math.toIntExact(DateUtils.getSecondsNextEarlyMorning()), TimeUnit.SECONDS);
            }
        } else {
            throw new BaseException("订单状态已发生变化");
        }
        otcOrderService.updateById(otcOrder);
        baseMapper.updateById(orderMatch);
        //TODO:存入消息
        AsyncManager.me().execute(AsyncFactory.sendSysChat(sellUserId, buyUserId, orderMatch.getOrderMatchNo(),
                SysOrderConstants.SysChatMsg.CANCEL_SUCCESS, MatchOrderStatus.CANCELLED));

        return Response.success();
    }


    /**
     * @param matchOrderNo
     * @Description: 买家确认 付款
     * @Param: [matchOrderNo]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: taylor
     * @Date: 2022/01/04
     */
    @Override
    public Response confirmPayment(String matchOrderNo) {
        //根据订单号查询到订单
        OtcOrderMatch orderMatch = baseMapper.selectById(matchOrderNo);
        //判断订单状态
        if (!orderMatch.getStatus().equals(MatchOrderStatus.WAITFORPAYMENT.getCode())) {
            return Response.error(MessageUtils.message("订单状态已发生变化"));
        }
        orderMatch.setPaymentTime(LocalDateTime.now());
        orderMatch.setStatus(MatchOrderStatus.PAID.getCode());
        String userId = ContextHandler.getUserId();
        String sellUserId = null;
        String buyUserId = null;
        if ("0".equals(orderMatch.getType())) {//买单
            if (userId.equals(orderMatch.getUserId())) {
                throw new BaseException(MessageUtils.message("订单错误"));
            }
            sellUserId = orderMatch.getUserId();
            buyUserId = userId;
        } else {
            if (!userId.equals(orderMatch.getUserId())) {
                throw new BaseException(MessageUtils.message("订单错误"));
            }
            sellUserId = orderMatch.getOtcOrderUserId();
            buyUserId = userId;
        }
        baseMapper.updateById(orderMatch);
        //TODO:存入消息
        AsyncManager.me().execute(AsyncFactory.sendSysChat(sellUserId, buyUserId, orderMatch.getOrderMatchNo(),
                SysOrderConstants.SysChatMsg.PAYMENT_SUCCESS, MatchOrderStatus.PAID));
        return Response.success();
    }


    /**
     * @param matchOrderNo
     * @Description: 卖家放款（放币）
     * @Param: [matchOrderNo]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: taylor
     * @Date: 2022/01/04
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//value="transactionManager1",
    public Response sellerPut(String matchOrderNo, boolean isAdmin) {
        String userId = ContextHandler.getUserId();
//        Date nowDate = new Date();
        OtcOrderMatch orderMatch = baseMapper.selectById(matchOrderNo);
        //判断订单状态
        if (!orderMatch.getStatus().equals(MatchOrderStatus.PAID.getCode())) {
            throw new BaseException(MessageUtils.message("订单状态已发生变化"));
        }
        if (MatchOrderStatus.APPEALING.getCode().equals(orderMatch.getStatus()) && !isAdmin) {
            return Response.error(MessageUtils.message("申诉中，不能放款"));
        }
        List<BalanceChange> cList = new ArrayList<>();
        //匹配订单分为 买单 和 卖单  //查询到otc 订单
        BigDecimal amount =orderMatch.getAmount();
        BigDecimal totalAmount = amount.add(orderMatch.getFee());
        if (orderMatch.getOrderNo() == null) {//一键卖币放行
            //手续费计算
            BalanceChange b1 = new BalanceChange();
            b1.setCoinName(orderMatch.getCoinName());
            b1.setFrozen(totalAmount.negate());
            b1.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            b1.setMainType(CoinConstants.MainType.NORECORD.getType());
            b1.setUserId(userId);
            cList.add(b1);
            if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                throw new AccountOperationBusyException();
            }
            CoinRecord rec = new CoinRecord();
            rec.setUserId(userId);
            rec.setCoinName(orderMatch.getCoinName());
            rec.setFee(BigDecimal.ZERO);
            rec.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            rec.setMainType(CoinConstants.MainType.TRANSFEROUT.getType());
            rec.setSonType(RecordSonType.TRANSFER_OUT);
            rec.setStatus(CoinConstants.RecordStatus.OK.getStatus());
            rec.setAmount(totalAmount.negate());
            recordService.save(rec);
            WebSocketHandle.accountChange(userId, orderMatch.getCoinName(), amount, RecordSonType.TRANSFER_OUT);

            orderMatch.setStatus(MatchOrderStatus.COMPLETED.getCode());
            orderMatch.setFinishTime(LocalDateTime.now());
            baseMapper.updateById(orderMatch);
            //给用户一个信号
            WebSocketHandle.orderStatusChange(userId, MatchOrderStatus.COMPLETED.getCode());
            return Response.success();
        }
        OtcOrder ezOtcOrder = otcOrderService.getById(orderMatch.getOrderNo());
        //减少冻结的币
        BigDecimal frozeAmount = ezOtcOrder.getFrozeAmount();
        BalanceChange b1 = new BalanceChange();
        b1.setCoinName(orderMatch.getCoinName());
        b1.setFrozen(amount.negate());
        b1.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
        b1.setMainType(CoinConstants.MainType.NORECORD.getType());
        b1.setFee(BigDecimal.ZERO);
        b1.setSonType(RecordSonType.TRANSFER_OUT);

        BalanceChange b2 = new BalanceChange();
        b2.setCoinName(orderMatch.getCoinName());
        b2.setAvailable(amount);
        b2.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
        b2.setMainType(CoinConstants.MainType.TRANSFERIN.getType());
        b2.setFee(BigDecimal.ZERO);
        b2.setSonType(RecordSonType.TRANSFER_IN);

        String userName = ContextHandler.getUserName();
        //改变OTC信息
        String sellUserId = null;
        String buyUserId = null;
        boolean flag = false;
        boolean flag1=false;
        LocalDateTime payTime = orderMatch.getPaymentTime();
        if ("0".equals(ezOtcOrder.getType())) {//买单
            if (isAdmin) {
                userId = orderMatch.getUserId();
                userName = userService.getById(userId).getUserName();
            }
            if (userId.equals(ezOtcOrder.getUserId())) {
                throw new BaseException("订单错误");
            }
            b1.setUserId(userId);
            cList.add(b1);
            //将amount币存入买家的账户中
            b2.setUserId(ezOtcOrder.getUserId());
            cList.add(b2);
            sellUserId = userId;
            buyUserId = ezOtcOrder.getUserId();
            //otc订单冻结币的剩余
            BigDecimal subtract = ezOtcOrder.getTotalAmount().subtract(ezOtcOrder.getQuotaAmount());
            if(ezOtcOrder.getMinimumLimit().compareTo(subtract) > 0){
                flag1 = true;
                ezOtcOrder.setStatus(1);
                ezOtcOrder.setEndTime(LocalDateTime.now());
                otcOrderService.updateById(ezOtcOrder);
            }
        } else if ("1".equals(ezOtcOrder.getType())) {  //卖单 需要广告商户进行放币
            if (isAdmin) {
                userId = ezOtcOrder.getUserId();
                userName = userService.getById(userId).getUserName();
            }
            if (!userId.equals(ezOtcOrder.getUserId())) {
                throw new BaseException("订单错误");
            }
            b1.setUserId(userId);//发布订单用户id
            cList.add(b1);
            //将amount币存入买家的账户中
            b2.setUserId(orderMatch.getUserId());
            cList.add(b2);
            sellUserId = userId;
            buyUserId = orderMatch.getUserId();

            //otc订单冻结币的剩余
            BigDecimal frozeNow = frozeAmount.subtract(amount);
            ezOtcOrder.setFrozeAmount(frozeNow);
            //如果冻结数量小于最新订单数量直接下架
            if (ezOtcOrder.getMinimumLimit().compareTo(ezOtcOrder.getFrozeAmount()) > 0) {
                flag = true;
                //TODO: 将冻结数量返回商户的资产中
                //解冻卖出的USDT
                BalanceChange b = new BalanceChange();
                b.setCoinName(ezOtcOrder.getCoinName());
                b.setAvailable(ezOtcOrder.getFrozeAmount());//返回冻结的数量
                b.setFrozen(ezOtcOrder.getFrozeAmount().negate());//解冻冻结的数量
                b.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
                b.setUserId(ezOtcOrder.getUserId());
                b.setMainType(CoinConstants.MainType.UNFREEZE.getType());
                b.setSonType(RecordSonType.TRANSACTION_UNFREEZE);
                b.setFee(BigDecimal.ZERO);
                cList.add(b);
                //改变订单状态
                ezOtcOrder.setStatus(1);
                ezOtcOrder.setEndTime(LocalDateTime.now());
            }
            otcOrderService.updateById(ezOtcOrder);
        }

        //改变订单状态
        orderMatch.setStatus(MatchOrderStatus.COMPLETED.getCode());
        orderMatch.setFinishTime(LocalDateTime.now());
        baseMapper.updateById(orderMatch);
        if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
            throw new AccountOperationBusyException();
        }
        CoinRecord rec = new CoinRecord();
        rec.setUserId(userId);
        rec.setCoinName(ezOtcOrder.getCoinName());
        rec.setFee(BigDecimal.ZERO);
        rec.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
        rec.setMainType(CoinConstants.MainType.TRANSFEROUT.getType());
        rec.setSonType(RecordSonType.TRANSFER_OUT);
        rec.setStatus(CoinConstants.RecordStatus.OK.getStatus());
        rec.setAmount(amount.negate());
        rec.setCreateBy(userName);
        recordService.save(rec);
        WebSocketHandle.accountChange(rec.getUserId(), rec.getCoinName(), rec.getAmount(), rec.getSonType());

        if (isAdmin) {
            AsyncManager.me().execute(AsyncFactory.sendSysChat(sellUserId, buyUserId, orderMatch.getOrderMatchNo(),
                    SysOrderConstants.SysChatMsg.APPEAL_PUT, MatchOrderStatus.COMPLETED));
        } else {
            AsyncManager.me().execute(AsyncFactory.sendSysChat(sellUserId, buyUserId, orderMatch.getOrderMatchNo(),
                    SysOrderConstants.SysChatMsg.RELEASE_SUCCESS, MatchOrderStatus.COMPLETED));
        }
        AsyncManager.me().execute(AsyncFactory.updateCount(sellUserId, buyUserId, payTime, LocalDateTime.now(), isAdmin, "0"));
        if (flag) {//系统下架提醒
            AsyncManager.me().execute(AsyncFactory.StationLetter(ezOtcOrder.getUserId(),
                    SysTipsConstants.TipsType.SYS_OFF_SHELF, ezOtcOrder.getOrderNo(),
                    ezOtcOrder.getFrozeAmount().setScale(8).toString()));
        }
        if (flag1) {//系统下架提醒
            AsyncManager.me().execute(AsyncFactory.StationLetter(ezOtcOrder.getUserId(),
                    SysTipsConstants.TipsType.SYS_OFF_SHELF_BY, ezOtcOrder.getOrderNo()));
        }

        return Response.success();
    }


    /**
     * 付款失败  后台修改订单为取消
     *
     * @param matchOrderNo
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public void paymentFail(String matchOrderNo) {
        OtcOrderMatch orderMatch = baseMapper.selectById(matchOrderNo);
        //判断订单状态
        if (!orderMatch.getStatus().equals(MatchOrderStatus.APPEALING.getCode())) {
            throw new BaseException("订单状态已发生变化");
        }
        //匹配订单分为 买单 和 卖单  //查询到otc 订单
        OtcOrder ezOtcOrder = otcOrderService.getById(orderMatch.getOrderNo());
        ezOtcOrder.setQuotaAmount(ezOtcOrder.getQuotaAmount().subtract(orderMatch.getAmount()));
        otcOrderService.updateById(ezOtcOrder);

        orderMatch.setStatus(MatchOrderStatus.CANCELLED.getCode());
        orderMatch.setFinishTime(LocalDateTime.now());
        baseMapper.updateById(orderMatch);

        String buyUserId;
        String sellUserId;
        //交易类型(0:买  1：卖)
        if ("0".equals(orderMatch.getType())) {
            buyUserId = orderMatch.getOtcOrderUserId();
            sellUserId = orderMatch.getUserId();
        } else {
            buyUserId = orderMatch.getUserId();
            sellUserId = orderMatch.getOtcOrderUserId();
        }
        List<BalanceChange> cList = new ArrayList<>();
        BalanceChange b = new BalanceChange();
        b.setCoinName(orderMatch.getCoinName());
        b.setAvailable(orderMatch.getAmount());
        b.setFrozen(orderMatch.getAmount().negate());
        b.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
        b.setMainType(CoinConstants.MainType.FROZEN.getType());
        b.setUserId(sellUserId);
        cList.add(b);
        if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
            throw new AccountOperationBusyException();
        }
        AsyncManager.me().execute(AsyncFactory.sendSysChat(sellUserId, buyUserId, orderMatch.getOrderMatchNo(),
                SysOrderConstants.SysChatMsg.APPEAL_CANCEL, MatchOrderStatus.CANCELLED));
        //降低买单完成率
        AsyncManager.me().execute(AsyncFactory.updateCount(sellUserId, buyUserId, orderMatch.getPaymentTime(), LocalDateTime.now(), true, "1"));
    }



    /***
     * @Description: 广告订单匹配订单
     * @Param: [matchOrderQueryReqDto]
     * @return: com.ezcoins.response.ResponseList<com.ezcoins.project.otc.entity.resp.OrderRecordRespDto>
     * @Author: Wanglei
     * @Date: 2021/7/8
     * @param matchOrderQueryReqDto
     */
    @Override
    public ResponseList<OrderRecordRespDto> adMatchOrder(AdMatchOrderQueryReqDto matchOrderQueryReqDto) {
        Page<OtcOrderMatch> page = new Page<>(matchOrderQueryReqDto.getPage(), matchOrderQueryReqDto.getLimit());
        LambdaQueryWrapper<OtcOrderMatch> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OtcOrderMatch::getOtcOrderUserId, ContextHandler.getUserId());
        String orderNo = matchOrderQueryReqDto.getOrderNo();
        if (StringUtils.hasText(orderNo)) {
            queryWrapper.eq(OtcOrderMatch::getOrderNo, orderNo);
        }
        String status = matchOrderQueryReqDto.getStatus();
        if (StringUtils.isEmpty(status)) {//已处理订单
            queryWrapper.and(wq -> wq
                    .eq(OtcOrderMatch::getStatus, MatchOrderStatus.COMPLETED.getCode()).or()
                    .eq(OtcOrderMatch::getStatus, MatchOrderStatus.ORDERBEENCANCELLED.getCode()).or()
                    .eq(OtcOrderMatch::getStatus, MatchOrderStatus.CANCELLED.getCode()).or()
                    .eq(OtcOrderMatch::getStatus, MatchOrderStatus.REFUSE.getCode()));
        }else if ("2".equals(status)) {//未处理
            queryWrapper.and(wq -> wq
                    .eq(OtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER.getCode()).or()
                    .eq(OtcOrderMatch::getStatus, MatchOrderStatus.APPEALING.getCode()).or()
                    .eq(OtcOrderMatch::getStatus, MatchOrderStatus.PAID.getCode()).or()
                    .eq(OtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT.getCode()));
        }
        queryWrapper.orderByDesc(OtcOrderMatch::getCreateTime);
        Page<OtcOrderMatch> matchPage = baseMapper.selectPage(page, queryWrapper);
        List<OtcOrderMatch> records = matchPage.getRecords();
        List<OrderRecordRespDto> orderRecordRespDtos = new ArrayList<>();
        records.forEach(e -> {
            OrderRecordRespDto orderRespDto = new OrderRecordRespDto();
            LambdaQueryWrapper<OtcOrderPayment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//            BeanUtils.copyBeanProp(orderRespDto, e);
            BeanUtils.copyProperties(e, orderRespDto);
//            orderRespDto.setAdvertisingName(e.getMatchAdvertisingName());
            if (orderRespDto.getStatus().equals(MatchOrderStatus.COMPLETED.getCode()) || orderRespDto.getStatus().equals(MatchOrderStatus.PAID.getCode())
                    || orderRespDto.getStatus().equals(MatchOrderStatus.APPEALING.getCode()) ) {
                ArrayList<OtcOrderPayment> list = new ArrayList<>();
                list.add(orderPaymentService.getById(e.getOrderPaymentId()));
                orderRespDto.setEzOtcOrderPayments(list);
            } else {
                if ("1".equals(e.getType())) {
                    lambdaQueryWrapper.eq(OtcOrderPayment::getOrderNo, e.getOrderMatchNo());
                } else {
                    lambdaQueryWrapper.eq(OtcOrderPayment::getOrderMatchNo, e.getOrderMatchNo());
                }
                lambdaQueryWrapper.eq(OtcOrderPayment::getOrderMatchNo, e.getOrderMatchNo());//有问题
                orderRespDto.setEzOtcOrderPayments(orderPaymentService.list(lambdaQueryWrapper));
            }
            orderRespDto.setNowTime(LocalDateTime.now());
            orderRecordRespDtos.add(orderRespDto);
        });
        return ResponseList.success(orderRecordRespDtos);
    }
}
