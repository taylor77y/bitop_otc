package com.bitop.otcapi.exception;


import com.bitop.otcapi.util.MessageUtils;

public class CommonException extends RuntimeException {
    public CommonException(String message) {
        super(MessageUtils.message(message));
    }
    public CommonException() {
        super(MessageUtils.message("非法操作"));
    }
}
