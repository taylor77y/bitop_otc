package com.bitop.otcapi.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static Date getBeForeTime(Integer prompt) {
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.MINUTE, prompt);// 5分钟之前的时间
        return beforeTime.getTime();
    }
}
