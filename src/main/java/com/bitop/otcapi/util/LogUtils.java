package com.bitop.otcapi.util;

import org.springframework.util.ObjectUtils;

/**
 * 处理并记录日志文件
 *
 *
 */
public class LogUtils
{
    public static String getBlock(Object msg)
    {
        if (ObjectUtils.isEmpty(msg))
        {
            msg = "";
        }
        return "[" + msg.toString() + "]";
    }
}
