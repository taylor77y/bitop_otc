package com.bitop.otcapi.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户信息异常类
 *
 *
 */
@Slf4j
public class UserException extends BaseException{
    private static final long serialVersionUID = 1L;

    public UserException(String defaultMessage, Object[] args){
        super("user", null, defaultMessage, args);
        log.info(null,args);
    }
}