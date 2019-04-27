package com.tc.reptile.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 21:26 2019/3/29
 */
public class DateUtil {
    public static final String FORMAT_TYPE_1 = "yyyy-MM-dd";
    public static final Integer DAY_Millis = 1000 * 60 * 60 * 24;

    /**
     * 获取某天最后一秒
     *
     * @return
     */
    public static Integer getDayEndSecond(DateTime time) {
        return (int) (new DateTime(time.getYear(), time.getMonthOfYear(), time.getDayOfMonth(), 23, 59, 59).getMillis() / 1000);
    }

    /**
     * 获取某天开始一秒
     *
     * @return
     */
    public static Integer getDayStartSecond(DateTime time) {
        return (int) (new DateTime(time.getYear(), time.getMonthOfYear(), time.getDayOfMonth(), 0, 0).getMillis() / 1000);
    }

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

    /**
     * 获取某个时间的秒数
     * @param time
     * @return
     */
    public static Integer getTimeSecond(DateTime time) {
        return (int)(time.getMillis() / 1000);
    }

    /***
     * @Author: Chensr
     * @Description: 将时间字符串转化为秒
     * @Date: 2019/3/29 21:30
     * @param date
     * @return: java.lang.Integer
     */
    public static Integer getDateSecond(String date, String format) {
        // 取得字符串中的数字
        Pattern p = Pattern.compile("[^0-9]");
        Matcher m = p.matcher(date);
        String num = m.replaceAll("");
        if (date.contains("天")) {
            return DateUtil.getCurrentSecond() - Integer.parseInt(num) * 60 * 60 * 24;
        } else if (date.contains("小时")) {
            return DateUtil.getCurrentSecond() - Integer.parseInt(num) * 60 * 60;
        } else {
            DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
            return (int) (DateTime.parse(date, formatter).getMillis() / 1000);
        }
    }

    public static void main(String[] args) {
        System.out.println(getDateSecond("04-28", "MM-dd"));
    }
}
