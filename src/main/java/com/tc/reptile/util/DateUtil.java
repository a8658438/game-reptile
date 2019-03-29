package com.tc.reptile.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 21:26 2019/3/29
 */
public class DateUtil {
    public static final String FORMAT_TYPE_1 ="yyyy-MM-dd";

    /***
     * @Author: Chensr
     * @Description: 获取当前时间秒数
     * @Date: 2019/3/29 21:29
     * @param
     * @return: java.lang.Integer
     */
    public static Integer getCurrentSecond() {
        return (int) Instant.now().getEpochSecond();
    }

    /***
     * @Author: Chensr
     * @Description: 将时间字符串转化为秒
     * @Date: 2019/3/29 21:30
     * @param date
     * @return: java.lang.Integer
     */
    public static Integer getDateSecond(String date, String format) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
        try {
            return  (int) (DateTime.parse(date, formatter).getMillis() / 1000);
        } catch (Exception e) {
            return DateUtil.getCurrentSecond();
        }
    }
}
