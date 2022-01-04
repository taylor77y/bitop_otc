package com.bitop.otcapi.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static Date getBeForeTime(Integer prompt) {
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.MINUTE, prompt);// 5分钟之前的时间
        return beforeTime.getTime();
    }


    /***
     * @Description:  获取当前时间到凌晨12点的秒数
     * @Param: []
     * @return: Long
     * @Author: taylor
     * @Date: 2022/01/04
     */
    public static Long getSecondsNextEarlyMorning() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
    }
}
