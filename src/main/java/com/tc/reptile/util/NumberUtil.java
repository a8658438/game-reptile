package com.tc.reptile.util;

import java.text.NumberFormat;

public class NumberUtil {
    /**
     * 计算百分比 num1 / num2
     * @param num1
     * @param num2
     * @return
     */

    public static String getPercent(Integer num1, Integer num2) {
        if (num1 == 0 && num2 == 0) {
            return "0%";
        }
        // 创建一个数值格式化对象

        NumberFormat numberFormat = NumberFormat.getInstance();

        // 设置精确到小数点后2位

        numberFormat.setMaximumFractionDigits(2);

        String result = numberFormat.format((float) num1 / (float) num2 * 100);
        return result + "%";
    }

    /**
     * 比对数字的大小 返回 1 0 -1
     * @param current
     * @param pre
     * @return
     */
    public static int equalsNum(Integer current, Integer pre) {
        return current - pre > 1 ? 1 : (current - pre == 0 ? 0: -1);
    }

}
