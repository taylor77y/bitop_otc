package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.constant.*;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.exception.AccountOperationBusyException;
import com.bitop.otcapi.exception.BaseException;
import com.bitop.otcapi.fcg.entity.OtcOrder;
import com.bitop.otcapi.fcg.entity.OtcOrderMatch;
import com.bitop.otcapi.fcg.entity.vo.BalanceChange;
import com.bitop.otcapi.fcg.mapper.OtcOrderMatchMapper;
import com.bitop.otcapi.fcg.service.CoinAccountService;
import com.bitop.otcapi.fcg.service.OtcOrderMatchService;
import com.bitop.otcapi.fcg.service.OtcOrderService;
import com.bitop.otcapi.manager.AsyncManager;
import com.bitop.otcapi.manager.factory.AsyncFactory;
import com.bitop.otcapi.redis.RedisCache;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.util.DateUtils;
import com.bitop.otcapi.util.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @Author: Wanglei
     * @Date: 2021/6/19
     */
    @Override
    public Response confirmPayment(String matchOrderNo) {
        //根据订单号查询到订单
        OtcOrderMatch orderMatch = baseMapper.selectById(matchOrderNo);
        //判断订单状态
        if (!orderMatch.getStatus().equals(MatchOrderStatus.WAITFORPAYMENT.getCode())) {
            return Response.error(MessageUtils.message("订单状态已发生变化"));
        }
        orderMatch.setPaymentTime(new Date());
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
}
