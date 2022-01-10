package com.bitop.otcapi.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.Map;

public class EthUsdtBtcUtils {
    private EthUsdtBtcUtils(){}
    public static BigDecimal getSymbol(String symbol){
        String s = HttpUtils.sendGet("https://api.huobi.pro/market/trade", "symbol="+symbol);
        Map mapTypes = JSON.parseObject(s);
        JSONObject tick = (JSONObject)mapTypes.get("tick");
        JSONArray data = (JSONArray)tick.get("data");
        JSONObject mapTypes2 = (JSONObject)data.get(0);
        return (BigDecimal) mapTypes2.get("price");
    }

    public static void main(String[] args) {

    }
}
