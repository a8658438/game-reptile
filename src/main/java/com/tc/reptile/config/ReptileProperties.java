package com.tc.reptile.config;

import com.tc.reptile.util.DateUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 11:41 2019/3/30
 */
@ConfigurationProperties(prefix = "reptile")
public class ReptileProperties {
    private String readTime;

    public Integer getReadTime() {
        return DateUtil.getDateSecond(readTime, DateUtil.FORMAT_TYPE_1);
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }
}
