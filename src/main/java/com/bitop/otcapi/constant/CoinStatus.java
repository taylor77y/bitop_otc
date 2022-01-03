package com.bitop.otcapi.constant;

public enum CoinStatus {
    ENABLE("0", "启用"),
    DISABLE("1", "禁用");

    private final String code;
    private final String info;

    CoinStatus(String code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public String getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }
}