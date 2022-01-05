package com.bitop.otcapi.websocket;

import lombok.Data;

import java.util.List;

@Data
public class WebSocketMessage {
    private List<String> topicList;
    private String data;
    private String type;
}
