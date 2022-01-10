package com.bitop.otcapi.constant;

public enum LoginType {
    WEB("WEB"), APP("APP");
    private String type;

    LoginType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static String get(String type) {
        LoginType[] values = values();
        for (LoginType value : values) {
            if (value.getType().equals(type)) {
                return value.getType();
            }
        }
        return null;
    }
}
