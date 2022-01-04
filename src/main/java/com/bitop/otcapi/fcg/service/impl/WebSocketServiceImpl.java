package com.bitop.otcapi.fcg.service.impl;

import com.bitop.otcapi.constant.RecordSonType;
import com.bitop.otcapi.constant.TopicSocket;
import com.bitop.otcapi.fcg.entity.vo.SendMessage;
import com.bitop.otcapi.fcg.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void sendMsg(String message) {
        if (message != null && message.trim().length() > 0) {
            simpMessagingTemplate.convertAndSend("/sub/chat", message);
        }
    }

    //订单状态变化
/*    public static void orderStatusChange(String userId, String status) {
        List<String> topicList = WebSocketFactory.userTopicMap.get(userId);
        if (topicList != null && topicList.size() > 0) {
            if (topicList.contains(TopicSocket.ODERSTATUS)) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setTopic(TopicSocket.ODERSTATUS);
                sendMessage.setData(status);
                WebSocketFactory.sendText(userId, JSON.toJSONString(sendMessage));
            }
        }else {
            String message=String.format("【ezcoins】 %s。", "您的订单已发送变化，请前往查看");
            send(userId, message);
        }
    }*/


    public void nowOrder(){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setTopic(TopicSocket.NEWORDER);
//        WebSocketFactory.sendMessageAll(JSON.toJSONString(sendMessage));
        if(StringUtils.hasLength(JSON.toJSONString(sendMessage))){
            simpMessagingTemplate.convertAndSend("/sub/chat", JSON.toJSONString(sendMessage));
        }
    }


    /**
     * 资产变化
     * @param userId
     * @param coinName
     * @param amount
     * @param sonType
     */
    public void accountChange(String userId, String coinName, BigDecimal amount, String sonType) {
        String message;
        if (sonType.equals(RecordSonType.SYS_AIRPORT)) {//系统空投
            message = String.format("%s。", "系统空投"+amount+coinName+"，资产已到账");
        } else if (sonType.equals(RecordSonType.SYS_DEDUCTION)) {//通过
            message = String.format("%s。", "系统已变跟你的资产"+amount+coinName);
        } else if (sonType.equals(RecordSonType.TRANSFER_IN)) {//通过
            message = String.format("%s。", "你购买的"+amount+coinName+"已到账");
        }else if (sonType.equals(RecordSonType.TRANSFER_OUT)) {//通过
            message = String.format("%s。", "出售"+amount+coinName+"成功");
        }else if (sonType.equals(RecordSonType.ORDINARY_WITHDRAWAL)) {//通过
            message = String.format("%s。", "提币成功"+amount+coinName+"成功");
        }else if (sonType.equals(RecordSonType.ORDINARY_RECHARGE)) {//通过
            message = String.format("%s。", "冲币成功"+amount+coinName+"成功，资产已到账");
        }else if (sonType.equals(RecordSonType.HANDLING_FEE)) {//通过
            message = String.format("%s。", "发布订单成功，已扣除"+amount+coinName+"手续费");
        }else if (sonType.equals(RecordSonType.TRANSACTION_FREEZE)) {//交易冻结
            message = String.format("%s。", "交易进行中，已冻结"+amount+coinName);
        }else if (sonType.equals(RecordSonType.TRANSACTION_UNFREEZE)) {//交易解冻
            message = String.format("%s。", "交易完成，已解冻"+amount+coinName);
        }else {
            return;
        }
        send(userId, message);
    }

    private void send(String userId, String message) {
        /*List<String> topicList = WebSocketFactory.userTopicMap.get(userId);
        if (topicList != null && topicList.size() > 0) {
            if (topicList.contains(TopicSocket.notification)) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setTopic(TopicSocket.notification);
                sendMessage.setData(message);
                simpMessagingTemplate.convertAndSend(userId, JSON.toJSONString(sendMessage));
            }
        }*/
    }
}
