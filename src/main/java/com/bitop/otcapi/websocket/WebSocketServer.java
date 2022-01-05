package com.bitop.otcapi.websocket;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;


/**
 * ping-pay websocket
 */
@Slf4j
@ServerEndpoint("/ezcoins/server")
@Component
public class WebSocketServer {

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        addOnlineCount();
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        subOnlineCount();
    }

    /**
     * 收到客户端消息后调用的方法
     * 使用说明：
     * type: subscribe
     * topic: nextCycleConsume
     * data: userId
     * <p>
     * type: heartbeat
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        WebSocketMessage webSocketMessage = JSONObject.parseObject(message, WebSocketMessage.class);
        String type = webSocketMessage.getType();
        if (type.equals("subscribe")) {//订阅
            WebSocketFactory.subscribe(webSocketMessage, session);
        } else if (type.equals("heartbeat")) {//心跳
            WebSocketFactory.heartbeat(session);
        } else if (type.equals("cancel")) {//取消
            WebSocketFactory.cancel(session);
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
    }

    public static synchronized void addOnlineCount() {
        WebSocketFactory.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketFactory.onlineCount--;
    }

}