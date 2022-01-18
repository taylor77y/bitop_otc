package com.bitop.otcapi.mq;

import com.bitop.otcapi.configuration.RabbitMQConfiguration;
import com.bitop.otcapi.constant.*;
import com.bitop.otcapi.exception.AccountOperationBusyException;
import com.bitop.otcapi.fcg.entity.OtcChatMsg;
import com.bitop.otcapi.fcg.entity.OtcOrder;
import com.bitop.otcapi.fcg.entity.OtcOrderMatch;
import com.bitop.otcapi.fcg.entity.vo.BalanceChange;
import com.bitop.otcapi.fcg.service.CoinAccountService;
import com.bitop.otcapi.fcg.service.OtcChatMsgService;
import com.bitop.otcapi.fcg.service.OtcOrderMatchService;
import com.bitop.otcapi.fcg.service.OtcOrderService;
import com.bitop.otcapi.redis.RedisCache;
import com.bitop.otcapi.util.DateUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description: 订单失效监听器
 * @author:  email: taylor77y@gmail.com
 * @createDate: 2020/12/18 8:30 上午
 * @updateUser:  email: taylor77y@gmail.com
 * @updateDate: 2020/12/18 8:30 上午
 * @updateRemark:   • RabbitMQ可以针对Queue和Message设置 x-message-tt，来控制消息的生存时间，如果超时，则消息变为dead letter
 *                  • RabbitMQ的Queue可以配置x-dead-letter-exchange 和x-dead-letter-routing-key（可选）两个参数，用来控制队列内出现了deadletter，则按照这两个参数重新路由。
 * @version: 1.0
 **/
@Component
@Slf4j
@RabbitListener(queues = RabbitMQConfiguration.deadQueueOrder)
public class OrderFailureListener {

    @Autowired
    private OtcOrderMatchService matchService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private OtcOrderService otcOrderService;

    @Autowired
    private OtcChatMsgService otcChatMsgService;

    @Autowired
    private CoinAccountService accountService;

    @RabbitHandler
    public void process(String order, Message message, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        String[] splitContent = new String[2];
        if (StringUtils.hasText(order)) {
            if (order.contains("_")) {
                splitContent = order.split("_");
            }
        }
        //截取字符串
        String otcOrderMatchNo = splitContent[0];
        OtcOrderMatch match = matchService.getById(otcOrderMatchNo);
        if (match == null) {
            return;
        }
        //取消订单
        if (match.getStatus().equals(MatchOrderStatus.WAITFORPAYMENT.getCode())) {
            //查询当前用户取消订单数量
            int count = 1;
            Object object = redisCache.getCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + match.getUserId());
            if (null != object) {
                count = (Integer) object + 1;
            }
            redisCache.setCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + match.getUserId(), count, Math.toIntExact(DateUtils.getSecondsNextEarlyMorning()), TimeUnit.SECONDS);
            match.setStatus(MatchOrderStatus.CANCELLED.getCode());
            //将订单匹配数量增加回去
            if ("2".equals(match.getOrderType())) {//一键卖币
                List<BalanceChange> cList = new ArrayList<>();
                BalanceChange b = new BalanceChange();
                BigDecimal add = match.getAmount().add(match.getFee());
                b.setCoinName(match.getCoinName());
                b.setAvailable(add);
                b.setFrozen(add.negate());
                b.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
                b.setMainType(CoinConstants.MainType.UNFREEZE.getType());
                b.setSonType(RecordSonType.TRANSACTION_UNFREEZE);
                b.setUserId(match.getUserId());
                cList.add(b);
                if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                    throw new AccountOperationBusyException();
                }
                List<OtcChatMsg> list = new ArrayList<>();
                OtcChatMsg ezOtcChatMsg1 = new OtcChatMsg();
                ezOtcChatMsg1.setOrderMatchNo(match.getOrderMatchNo());
                ezOtcChatMsg1.setReceiveUserId(match.getUserId());
                ezOtcChatMsg1.setSendText(SystemOrderTips.SYSTEM_CANCEL);
                list.add(ezOtcChatMsg1);
                matchService.updateById(match);
                otcChatMsgService.sendSysChat(list, MatchOrderStatus.COMPLETED.getCode());
            } else {
                OtcOrder ezOtcOrder = otcOrderService.getById(match.getOrderNo());
                ezOtcOrder.setQuotaAmount(ezOtcOrder.getQuotaAmount().subtract(match.getAmount()));
                if (ezOtcOrder.getType().equals("0")) {//买
                    List<BalanceChange> cList = new ArrayList<>();
                    BalanceChange b = new BalanceChange();
                    BigDecimal add = match.getAmount().add(match.getFee());
                    b.setCoinName(match.getCoinName());
                    b.setAvailable(add);
                    b.setFrozen(add.negate());
                    b.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
                    b.setMainType(CoinConstants.MainType.UNFREEZE.getType());
                    b.setSonType(RecordSonType.TRANSACTION_UNFREEZE);
                    b.setUserId(match.getUserId());
                    cList.add(b);
                    if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                        throw new AccountOperationBusyException();
                    }
                }
                List<OtcChatMsg> list = new ArrayList<>();
                OtcChatMsg ezOtcChatMsg1 = new OtcChatMsg();
                OtcChatMsg ezOtcChatMsg2 = new OtcChatMsg();
                ezOtcChatMsg1.setOrderMatchNo(match.getOrderMatchNo());
                ezOtcChatMsg1.setReceiveUserId(match.getUserId());
                ezOtcChatMsg1.setSendText(SystemOrderTips.SYSTEM_CANCEL);
                ezOtcChatMsg2.setOrderMatchNo(match.getOrderMatchNo());
                ezOtcChatMsg2.setReceiveUserId(match.getOtcOrderUserId());
                ezOtcChatMsg2.setSendText(SystemOrderTips.SYSTEM_CANCEL_2);
                list.add(ezOtcChatMsg1);
                list.add(ezOtcChatMsg2);
                otcChatMsgService.sendSysChat(list, MatchOrderStatus.COMPLETED.getCode());
                matchService.updateById(match);
                otcOrderService.updateById(ezOtcOrder);
            }
        }
        //接单广告取消
        if (match.getStatus().equals(MatchOrderStatus.PENDINGORDER.getCode())) {
            match.setStatus(MatchOrderStatus.ORDERBEENCANCELLED.getCode());
            matchService.updateById(match);
            List<OtcChatMsg> list = new ArrayList<>();
            OtcChatMsg ezOtcChatMsg1 = new OtcChatMsg();
            OtcChatMsg ezOtcChatMsg2 = new OtcChatMsg();
            ezOtcChatMsg1.setOrderMatchNo(match.getOrderMatchNo());
            ezOtcChatMsg1.setReceiveUserId(match.getUserId());
            ezOtcChatMsg1.setSendText(SystemOrderTips.SYSTEM_CANCEL);
            ezOtcChatMsg2.setOrderMatchNo(match.getOrderMatchNo());
            ezOtcChatMsg2.setReceiveUserId(match.getOtcOrderUserId());
            ezOtcChatMsg2.setSendText(SystemOrderTips.SYSTEM_CANCEL_2);
            list.add(ezOtcChatMsg1);
            list.add(ezOtcChatMsg2);
            otcChatMsgService.sendSysChat(list, MatchOrderStatus.ORDERBEENCANCELLED.getCode());
        }
    }
}