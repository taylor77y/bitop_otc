package com.bitop.otcapi.fcg.service.impl;

import com.bitop.otcapi.constant.TopicSocket;
import com.bitop.otcapi.fcg.entity.vo.SendMessage;
import com.bitop.otcapi.fcg.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    private SimpMessagingTemplate webSocketTemplate;

    public void sendMsg(String message) {
        if (message != null && message.trim().length() > 0) {
            webSocketTemplate.convertAndSend("/sub/chat", message);
        }
    }

    public void nowOrder(){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setTopic(TopicSocket.NEWORDER);
//        WebSocketFactory.sendMessageAll(JSON.toJSONString(sendMessage));
        if(StringUtils.hasLength(JSON.toJSONString(sendMessage))){
            webSocketTemplate.convertAndSend("/sub/chat", JSON.toJSONString(sendMessage));
        }
    }
}
