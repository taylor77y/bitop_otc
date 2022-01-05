package com.bitop.otcapi.fcg.service.impl;

import com.bitop.otcapi.fcg.entity.OtcChatMsg;
import com.bitop.otcapi.fcg.service.OtcChatMsgService;
import com.bitop.otcapi.websocket.WebSocketHandle;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OtcChatMsgServiceImpl implements OtcChatMsgService {

    private static final String MESSAGE_COLLECTION_NAME = "otc_chat_msg";

    @Resource
    private MongoTemplate mongoTemplate;
    /**
     * 存入系统提示消息
     *
     * @param chatMsgList
     * @param status
     */
    @Override
    public void sendSysChat(List<OtcChatMsg> chatMsgList, String status) {
        mongoTemplate.insert(chatMsgList, MESSAGE_COLLECTION_NAME);
//        this.saveBatch(chatMsgList);
        if (status!=null){
            for (OtcChatMsg ezOtcChatMsg:chatMsgList){
                String receiveUserId = ezOtcChatMsg.getReceiveUserId();
                //给用户一个信号
                WebSocketHandle.orderStatusChange(receiveUserId, status);
                //给用户一个信号
                WebSocketHandle.toChatWith(receiveUserId, ezOtcChatMsg.getOrderMatchNo());
            }
        }
    }
}
