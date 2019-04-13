package com.tc.reptile.constant;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 22:56 2019/4/13
 */
public enum YystvBordEnum {
    VEDIO(1,"视频节目"),
    PUSH_GAME(2, "推游"),
    GAME_STORY(3, "游戏史"),
    BIG_NEWS(4, "大事件"),
    CULTURE(5, "文化"),
    INTERESTING_NEWS(6, "趣闻"),
    CLASSICAL(7, "经典");
    private Integer value;
    private String name;

    YystvBordEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public static String getName(Integer value) {
        for (YystvBordEnum bordEnum : YystvBordEnum.values()) {
            if (bordEnum.getValue().equals(value)) {
                return bordEnum.getName();
            }
        }
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }}
