package com.bitop.otcapi.fcg.service;

import java.math.BigDecimal;

public interface WebSocketService {

    void sendMsg(String message);

    void nowOrder();

    void accountChange(String userId, String coinName, BigDecimal amount, String sonType);
}
