package com.bitop.otcapi.exception;

/**
 * 用户密码不正确或不符合规范异常类
 *
 *
 */
public class UserPasswordNotMatchException extends UserException{

    private static final long serialVersionUID = 1L;

    public UserPasswordNotMatchException()
    {
        super("密码错误", null);
    }
}

