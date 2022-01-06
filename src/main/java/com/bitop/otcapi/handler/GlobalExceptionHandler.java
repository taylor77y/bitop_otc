package com.bitop.otcapi.handler;


import com.bitop.otcapi.exception.BaseException;
import com.bitop.otcapi.exception.CustomException;
import com.bitop.otcapi.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.security.auth.login.AccountExpiredException;
import java.nio.file.AccessDeniedException;

/**
 * 全局异常处理器
 *
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 基础异常
     */
    @ExceptionHandler(BaseException.class)
    public Response baseException(BaseException e){
        Integer code=e.getCode()==null? HttpStatus.INTERNAL_SERVER_ERROR.value(): Integer.parseInt(e.getCode());
        return Response.error(e.getMessage(),code);
    }
    /**
     * 业务异常
     */
    @ExceptionHandler(CustomException.class)
    public Response businessException(CustomException e){
        if (StringUtils.isEmpty(e.getCode()))
        {
            return Response.error(e.getMessage());
        }
        return Response.error(e.getMessage(),e.getCode());
    }

//    @ExceptionHandler(NoHandlerFoundException.class)
//    public Response handlerNoFoundException(Exception e){
//        log.error(e.getMessage(), e);
//        return Response.error(HttpStatus.NOT_FOUND, MessageUtils.message("path.not.exist"));//路径不存在，请检查路径是否正确
//    }
//
//    @ExceptionHandler(AccessDeniedException.class)
//    public Response handleAuthorizationException(AccessDeniedException e){
//        log.error(e.getMessage());
//        return Response.error(HttpStatus.FORBIDDEN, MessageUtils.message("no.permission"));//没有权限，请联系管理员授权
//    }

    @ExceptionHandler(AccountExpiredException.class)
    public Response handleAccountExpiredException(AccountExpiredException e){
        log.error(e.getMessage(), e);
        return Response.error(e.getMessage());
    }

//    @ExceptionHandler(UsernameNotFoundException.class)
//    public BaseResponse handleUsernameNotFoundException(UsernameNotFoundException e){
//        log.error(e.getMessage(), e);
//        return BaseResponse.error(e.getMessage());
//    }

    @ExceptionHandler(Exception.class)
    public Response handleException(Exception e) {
        log.error(e.getMessage(), e);
        return Response.error(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public Response validatedBindException(BindException e)
    {
        log.error(e.getMessage());
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return Response.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object validExceptionHandler(MethodArgumentNotValidException e){
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return Response.error(message);
    }

}
