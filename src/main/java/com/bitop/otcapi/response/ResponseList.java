package com.bitop.otcapi.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * @Author: taylor
 * @Email: taylor77y@gmail.com
 * @Description:
 * @Date:2021/6/9 16:13
 * @Version:1.0
 */
@Data
public class ResponseList<T> {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "是否成功")
    private Boolean success;

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回数据")
    private List<T> data ;

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    private ResponseList(){
    }

    //成功静态方法
    public static <T> ResponseList<T> success(List<T> t) {
        ResponseList<T> r = new ResponseList<T>();
        r.setSuccess(true);
        r.setCode(HttpStatus.OK.value());
        r.setMessage(HttpStatus.OK.getReasonPhrase());
        r.data= t;
        return r;
    }

    //成功静态方法
    public static <T> ResponseList<T> error(String message,List<T> t) {
        ResponseList<T> r = new ResponseList<T>();
        r.setSuccess(true);
        r.setCode(HttpStatus.OK.value());
        r.setMessage(HttpStatus.OK.getReasonPhrase());
        if (t!=null){
            r.data=t;
        }
        return r;
    }


}
