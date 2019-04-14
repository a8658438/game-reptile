package com.tc.reptile.util;

import org.springframework.util.StringUtils;

public class HtmlUtil {

    public static String delHtmlTag(String str){
        String newstr = "";
        newstr = str.replaceAll("<[.[^>]]*>","");
        newstr = newstr.replaceAll(" ", "");
        return newstr.replaceAll("(\\\r\\\n|\\\r|\\\n|\\\n\\\r)", "");
    }

    /**note
     * 获取html中的缩略文字
     * @param html
     * @return
     */
    public static String getBreviary(String html) {
        if (StringUtils.isEmpty(html)) {
            return null;
        }
        return delHtmlTag(html).substring(0, 100);
    }
}
