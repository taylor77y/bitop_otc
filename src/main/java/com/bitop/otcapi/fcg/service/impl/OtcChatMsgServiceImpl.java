package com.bitop.otcapi.fcg.service.impl;

import com.bitop.otcapi.fcg.entity.OtcChatMsg;
import com.bitop.otcapi.fcg.service.OtcChatMsgService;
import com.bitop.otcapi.websocket.WebSocketHandle;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OtcChatMsgServiceImpl implements OtcChatMsgService {

    private static final String MESSAGE_COLLECTION_NAME = "otc_chat_msg";

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public List<OtcChatMsg> pageByNo(String orderMatchNo) {
        Query query = Query.query(Criteria.where("orderMatchNo").is(orderMatchNo));
        // 每页五个
//        Pageable pageable = new PageRequest(pageIndex, pageSize); // get 5 profiles on a page
        // 排序
//        query.with(new Sort(Sort.Direction.ASC, CONSTS.DEVICE_SERIAL_FIELD, CONSTS.DOMAINID_FIELD));
        return mongoTemplate.find(query, OtcChatMsg.class);
    }

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
