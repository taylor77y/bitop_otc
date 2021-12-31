package com.bitop.otcapi.constant;

public enum PaymentMethod {

    BANK (1, "银行"), ALIPAY(2, "支付宝"),  WECHAT(3, "微信");
    private final Integer code;
    private final String info;
    PaymentMethod(Integer code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public Integer getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }

}
