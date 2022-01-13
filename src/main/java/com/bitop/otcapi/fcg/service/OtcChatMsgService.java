package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcChatMsg;
import com.bitop.otcapi.fcg.entity.resp.ChatMsgRespDto;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponseList;

import java.util.List;

public interface OtcChatMsgService extends IService<OtcChatMsg> {


    List<OtcChatMsg> pageByNo(String orderMatchNo);

    /**
     * 发送 聊天信息 文字/图片
     * @param chatMsgList
     * @return
     */
    Response sendChat(List<OtcChatMsg> chatMsgList, String sendId);


    /**
     * 存入系统提示消息
     */
    void sendSysChat(List<OtcChatMsg> chatMsgList, String status);


    /**
     * 根据匹配订单id查询聊天记录
     * @param orderMatchNo
     * @return
     */
    ResponseList<ChatMsgRespDto> chatMsg(String orderMatchNo);
}
