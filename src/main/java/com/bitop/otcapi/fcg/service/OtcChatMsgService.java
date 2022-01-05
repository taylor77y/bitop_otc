package com.bitop.otcapi.fcg.service;

import com.bitop.otcapi.fcg.entity.OtcChatMsg;

import java.util.List;

public interface OtcChatMsgService {

    List<OtcChatMsg> pageByNo(String orderMatchNo);

    /**
     * 存入系统提示消息
     */
    void sendSysChat(List<OtcChatMsg> chatMsgList, String status);
}
