package com.bitop.otcapi.fcg.service.impl;

import com.bitop.otcapi.fcg.entity.OtcChatMsg;
import com.bitop.otcapi.fcg.service.OtcChatMsgService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OtcChatMsgServiceImpl implements OtcChatMsgService {
    @Override
    public void sendSysChat(List<OtcChatMsg> chatMsgList, String status) {
        return;
    }
}
