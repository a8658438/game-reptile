package com.tc.reptile.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Chensr
 * @Description: 检测文章中的游戏名称
 * @Date: Create in 15:43 2019/3/30
 */
public class RegexUtil {
    public static String REGEX_GAME_NAME = "《(.*?)》";
    public static String REGEX_CHINESE = "[\u4e00-\u9fa5]";// 中文正则
    public static String REGEX_ENGLISH = "[a-zA-z]"; // 英文正则

    /***
     * @Author: Chensr
     * @Description: 删除中文
     * @Date: 2019/4/28 0:40
     * @param str
     * @return: java.lang.String
    */
    public static String replaceReg(String str, String reg) {
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(str);
        return mat.replaceAll("").trim();
    }

    /***
     * @Author: Chensr
     * @Description: 正则匹配获取游戏名称
     * @Date: 2019/3/30 15:51
     * @param html
     * @return: java.util.Set<java.lang.String>
    */
    public static Set<String> getGames(String html) {
        Pattern pattern = Pattern.compile(REGEX_GAME_NAME);
        Matcher matcher = pattern.matcher(html);
        Set<String> set = new HashSet<>();
        while (matcher.find()) {
            String book = matcher.group(1);
            set.add(HtmlUtil.delHtmlTag(book).replace(" ", ""));
        }
        return set;
    }
}
