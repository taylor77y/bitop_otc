package com.bitop.otcapi.fcg.entity.vo;

import lombok.Data;

@Data
public class SendMessage {
    private String topic;
    private Object data;
}
