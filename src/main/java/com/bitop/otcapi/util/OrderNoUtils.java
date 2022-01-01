package com.bitop.otcapi.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Date;

public class OrderNoUtils {

    private static DateTimeFormatter DATE_FORMATTER;

    /**
     * 获取订单号
     * @return
     */
    public static String getOrderNo() {
        Instant now = Instant.now(NanoClock.INSTANCE);
        return DATE_FORMATTER.format(now) + String.format("%09d", now.getLong(ChronoField.NANO_OF_SECOND));
    }
}
