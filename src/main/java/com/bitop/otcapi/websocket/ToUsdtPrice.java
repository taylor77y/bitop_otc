package com.bitop.otcapi.websocket;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ToUsdtPrice {
    private String name;
    private BigDecimal price;

}
