package com.bitop.otcapi.exception;

import com.bitop.otcapi.util.MessageUtils;
import org.springframework.util.StringUtils;

/**
 * 基础异常
 *
 *
 */
public class BaseException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    /**
     * 所属模块
     */
    private String module;

    /**
     * 错误码
     */
    private String code;


    /**
     * 错误消息
     */
    private String defaultMessage;

    /**
     * 错误消息对应的参数
     */
    private Object[] args;


    public BaseException(String module, String code, String defaultMessage, Object[] args){
        this.module = module;
        this.code = code;
        this.args = args;
        this.defaultMessage = defaultMessage;
    }

    public BaseException(String module, String code, Object[] args){
        this(module, code, null, args);
    }

    public BaseException(String module, String defaultMessage)
    {
        this(module, null, defaultMessage, null);
    }

    public BaseException(String defaultMessage, Object[] args)
    {
        this(null, null, defaultMessage, args);
    }

    public BaseException(Object[] args,String code,String defaultMessage)
    {
        this(null, code, defaultMessage, args);
    }

    public BaseException(String defaultMessage){
        this(null, null, defaultMessage, null);
    }

    @Override
    public String getMessage()
    {
        String message = null;
        if (!StringUtils.isEmpty(defaultMessage))
        {
            message = MessageUtils.message(defaultMessage, args);
        }
        return message;
    }

    public String getModule()
    {
        return module;
    }

    public String getCode()
    {
        return code;
    }

    public Object[] getArgs()
    {
        return args;
    }

    public String getDefaultMessage()
    {
        return defaultMessage;
    }
}
