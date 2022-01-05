package com.bitop.otcapi.websocket;

import lombok.Data;

@Data
public class SendMessage {
    private String topic;
    private Object data;
}
