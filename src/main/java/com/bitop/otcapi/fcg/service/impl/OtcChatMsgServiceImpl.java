package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.fcg.entity.OtcBankCard;
import com.bitop.otcapi.fcg.entity.OtcChatMsg;
import com.bitop.otcapi.fcg.entity.OtcOrderMatch;
import com.bitop.otcapi.fcg.entity.resp.ChatMsgRespDto;
import com.bitop.otcapi.fcg.mapper.OtcBankCardMapper;
import com.bitop.otcapi.fcg.mapper.OtcChatMsgMapper;
import com.bitop.otcapi.fcg.service.OtcAdvertisingBusinessService;
import com.bitop.otcapi.fcg.service.OtcBankCardService;
import com.bitop.otcapi.fcg.service.OtcChatMsgService;
import com.bitop.otcapi.fcg.service.OtcOrderMatchService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponseList;
import com.bitop.otcapi.util.BeanUtils;
import com.bitop.otcapi.websocket.WebSocketHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class OtcChatMsgServiceImpl extends ServiceImpl<OtcChatMsgMapper, OtcChatMsg> implements OtcChatMsgService {

    private static final String MESSAGE_COLLECTION_NAME = "otc_chat_msg";

    @Resource
    private MongoTemplate mongoTemplate;

    @Autowired
    private OtcAdvertisingBusinessService businessService;

    @Autowired
    private OtcOrderMatchService orderMatchService;

    /**
     * 发送 聊天信息 文字/图片 内容类型(0:图片 1：文字)
     *
     * @param chatMsgList
     * @return
     */
    @Override
    public Response sendChat(List<OtcChatMsg> chatMsgList, String sendId) {
        for (OtcChatMsg ezOtcChatMsg:chatMsgList){
            if (StringUtils.hasText(sendId)){
                ezOtcChatMsg.setSendUserId(sendId);
            }
            baseMapper.insert(ezOtcChatMsg);
            //给用户一个信号
            WebSocketHandle.toChatWith(ezOtcChatMsg.getReceiveUserId(), ezOtcChatMsg.getOrderMatchNo());
        }
        return Response.success();
    }

    /**
     * 存入系统提示消息
     *
     * @param chatMsgList
     * @param status
     */
    @Override
    public void sendSysChat(List<OtcChatMsg> chatMsgList, String status) {
        this.saveBatch(chatMsgList);
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

    /**
     * 根据匹配订单id查询聊天记录
     *
     * @param orderMatchNo
     * @return
     */
    @Override
    public ResponseList<ChatMsgRespDto> chatMsg(String orderMatchNo) {
        //查询订单
        OtcOrderMatch match = orderMatchService.getById(orderMatchNo);
        String userId = match.getUserId();
        String otcOrderUserId = match.getOtcOrderUserId();//商户

        String userId1 = ContextHandler.getUserId();

        String sendName =null;
        String receiveName =null;
        String u = null;
        if (otcOrderUserId.equals(userId1)) {
            u = userId;
            sendName=match.getAdvertisingName();
            receiveName=match.getMatchAdvertisingName();
        } else {
            u = otcOrderUserId;
            receiveName=match.getAdvertisingName();
            sendName=match.getMatchAdvertisingName();
        }
        //查询双方的
        LambdaQueryWrapper<OtcChatMsg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OtcChatMsg::getOrderMatchNo, orderMatchNo);
        queryWrapper.orderByDesc(OtcChatMsg::getCreateTime);
        List<OtcChatMsg> list = baseMapper.selectList(queryWrapper);

        List<ChatMsgRespDto> list1 = new ArrayList<>();
        String finalU = u;
        String finalSendName = sendName;
        String finalReceiveName = receiveName;
        list.forEach(e -> {
            if (!e.getReceiveUserId().equals(userId1) && "0".equals(e.getIsSystem())) {
            } else {
                ChatMsgRespDto chatMsgReqDto = new ChatMsgRespDto();
//                BeanUtils.copyBeanProp(chatMsgReqDto, e);
                BeanUtils.copyProperties(e, chatMsgReqDto);
                if (chatMsgReqDto.getReceiveUserId().equals(userId1)) {
                    chatMsgReqDto.setReceiveUserId(finalU);
                    chatMsgReqDto.setSendUserId(userId1);
                } else {
                    chatMsgReqDto.setSendUserId(finalU);
                    chatMsgReqDto.setReceiveUserId(chatMsgReqDto.getSendUserId());
                }
                chatMsgReqDto.setSendName(finalSendName);//我的名
                chatMsgReqDto.setReceiveName(finalReceiveName);//对面的名
                list1.add(chatMsgReqDto);
            }
        });

        return ResponseList.success(list1);
    }


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
//    @Override
//    public void sendSysChat(List<OtcChatMsg> chatMsgList, String status) {
//        mongoTemplate.insert(chatMsgList, MESSAGE_COLLECTION_NAME);
////        this.saveBatch(chatMsgList);
//        if (status!=null){
//            for (OtcChatMsg ezOtcChatMsg:chatMsgList){
//                String receiveUserId = ezOtcChatMsg.getReceiveUserId();
//                //给用户一个信号
//                WebSocketHandle.orderStatusChange(receiveUserId, status);
//                //给用户一个信号
//                WebSocketHandle.toChatWith(receiveUserId, ezOtcChatMsg.getOrderMatchNo());
//            }
//        }
//    }
}
