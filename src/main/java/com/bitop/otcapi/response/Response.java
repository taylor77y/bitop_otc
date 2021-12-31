package com.bitop.otcapi.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @Author: taylor
 * @Email: taylor77y@gmail.com
 * @Description:
 * @Date:2021/6/9 16:13
 * @Version:1.0
 */
@Data
public class Response<T>{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "是否成功")
    private Boolean success;

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回数据")
    private T data ;

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    private Response(){
    }
    //成功静态方法
    public static <T> Response<T> success(T t) {
        Response<T> r = new Response<T>();
        r.setSuccess(true);
        r.setCode(HttpStatus.OK.value());
        r.setMessage(HttpStatus.OK.getReasonPhrase());
        r.data= t;
        return r;
    }

    public static <T> Response<T> success() {
        Response<T> r = new Response<T>();
        r.setSuccess(true);
        r.setCode(HttpStatus.OK.value());
        r.setMessage(HttpStatus.OK.getReasonPhrase());
        return r;
    }

    //成功静态方法
    public static <T> Response<T> success(String msg, T t) {
        Response<T> r = new Response<T>();
        r.setSuccess(true);
        r.setCode(HttpStatus.OK.value());
        r.setMessage(msg);
        r.data= t;
        return r;
    }


    //成功静态方法
    public static <T> Response<T> error(String message) {
        Response<T> r = new Response<T>();
        r.setSuccess(false);
        r.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        r.setMessage(message);
        return r;
    }

    public static <T> Response<T> error() {
        Response<T> r = new Response<T>();
        r.setSuccess(false);
        r.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        r.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        return r;

    }

    //成功静态方法
    public static <T> Response<T> error(String message,Integer code) {
        Response<T> r = new Response<T>();
        r.setSuccess(false);
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    public Response<T> message(String message){
        this.setMessage(message);
        return this;
    }


    //成功静态方法
    public static <T> Response<T> error(String message,T t) {
        Response<T> r = new Response<T>();
        r.setSuccess(false);
        r.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        r.setMessage(message);
        r.setData(t);
        return r;
    }

    public Response<T> code(Integer code){
        this.setCode(code);
        return this;
    }
}
