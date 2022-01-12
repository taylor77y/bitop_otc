package com.bitop.otcapi.exception;

public class CheckException {

    public static void check(boolean flag, String message, OnOperate onOperate) {
        if (flag) {
            if (onOperate != null) {
                onOperate.operate();
            }
            throw new CommonException(message);
        }
    }

    public interface OnOperate {
        void operate();
    }
}
