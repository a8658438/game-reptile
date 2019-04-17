package com.tc.reptile.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtil {

    public static String delHtmlTag(String str) {

        if (str == null)
            return null;
        String htmlStr = str; // 含html标签的字符串
        String textStr = "";
        Pattern p_script, p_style, p_html, p_special;
        Matcher m_style, m_html, m_special;
//定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
        String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
//定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
        String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
// 定义HTML标签的正则表达式
        String regEx_html = "<[^>]+>";
// 定义一些特殊字符的正则表达式 如：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        String regEx_special = "\\&[a-zA-Z]{1,10};";

        p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签
        p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签
        p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签
        p_special = Pattern.compile(regEx_special, Pattern.CASE_INSENSITIVE);
        m_special = p_special.matcher(htmlStr);
        htmlStr = m_special.replaceAll(""); // 过滤特殊标签

        // 过滤换行空格 ，替换冒号
        htmlStr = htmlStr.replaceAll(" ", "");
        htmlStr = htmlStr.replaceAll("(\\\r\\\n|\\\r|\\\n|\\\n\\\r)", "");
        htmlStr = htmlStr.replaceAll(":", "：");
        textStr = htmlStr;
        return textStr;
    }

    /**
     * note
     * 获取html中的缩略文字
     *
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
