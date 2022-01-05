package com.bitop.otcapi.manager.factory;

import com.bitop.otcapi.constant.MatchOrderStatus;
import com.bitop.otcapi.constant.SysOrderConstants;
import com.bitop.otcapi.constant.SysTipsConstants;
import com.bitop.otcapi.fcg.entity.OtcChatMsg;
import com.bitop.otcapi.fcg.entity.SysTips;
import com.bitop.otcapi.fcg.service.OtcAdvertisingBusinessService;
import com.bitop.otcapi.fcg.service.OtcChatMsgService;
import com.bitop.otcapi.fcg.service.SysTipsService;
import com.bitop.otcapi.util.SpringUtils;
import com.bitop.otcapi.websocket.WebSocketHandle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class AsyncFactory {

    /**
     * 站内信
     */
    public static TimerTask StationLetter(String userId, SysTipsConstants.TipsType tipsType, Object ... args){
        SysTips ezSysTips=new SysTips();
        ezSysTips.setUserId(userId);
        ezSysTips.setTitle("系统信息");
        ezSysTips.setContent(String.format(tipsType.getRemark(),args));
        return new TimerTask(){
            @Override
            public void run(){
                SpringUtils.getBean(SysTipsService.class).save(ezSysTips);
            }
        };
    }


    /**
     * 发送信息
     */
    public static TimerTask sendSysChat(String sellUserId, String buyUserId, String orderMatchNo,
                                        SysOrderConstants.SysChatMsg chatMsg, MatchOrderStatus status){
        OtcChatMsg otcChatMsg_sell = new OtcChatMsg();//otcChatMsg_sell
        OtcChatMsg otcChatMsg_buy = new OtcChatMsg();
        otcChatMsg_sell.setReceiveUserId(sellUserId);
        otcChatMsg_sell.setOrderMatchNo(orderMatchNo);
        otcChatMsg_sell.setSendText(chatMsg.getSellTips());
        otcChatMsg_buy.setReceiveUserId(buyUserId);
        otcChatMsg_buy.setOrderMatchNo(orderMatchNo);
        otcChatMsg_buy.setSendText(chatMsg.getBuyTips());
        List<OtcChatMsg> list = new ArrayList<>();
        list.add(otcChatMsg_sell);
        list.add(otcChatMsg_buy);
        //给用户一个信号
        WebSocketHandle.orderStatusChange(sellUserId, status.getCode());
        WebSocketHandle.orderStatusChange(buyUserId, status.getCode());
        return new TimerTask(){
            @Override
            public void run(){
                SpringUtils.getBean(OtcChatMsgService.class).sendSysChat(list,null);
            }
        };
    }


    /**
     * 修改商户卖单买单
     */
    public static TimerTask updateCount(String sellUserId, String buyUserId, Date payTime, Date finishTime, boolean isAdmin, String status) {
        return new TimerTask(){
            @Override
            public void run(){
                SpringUtils.getBean(OtcAdvertisingBusinessService.class)
                        .updateCount(sellUserId,buyUserId,payTime,finishTime,isAdmin,status);
            }
        };
    }
}
