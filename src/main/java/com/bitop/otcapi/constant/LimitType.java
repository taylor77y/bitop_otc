package com.bitop.otcapi.constant;

public enum LimitType {
    NOLIMIT("-1", "无验证"),
    LOGINLIMIT("0", "登录封禁"),
    WITHDRAWLIMIT("1", "提现封禁"),
    ORDERLIMIT("2", "发布广告封禁"),
    BUSINESSLIMIT("3", "买卖封禁");
    private final String code;
    private final String info;

    LimitType(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

}