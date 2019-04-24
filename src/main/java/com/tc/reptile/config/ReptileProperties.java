package com.tc.reptile.config;

import com.tc.reptile.util.DateUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 11:41 2019/3/30
 */
@ConfigurationProperties(prefix = "config.reptile")
public class ReptileProperties {
    private String readTime;
    private Integer countLimit; // 爬取次数限制

    public Integer getReadTime() {
        return DateUtil.getDateSecond(readTime, DateUtil.FORMAT_TYPE_1);
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }

    public Integer getCountLimit() {
        return countLimit;
    }

    public void setCountLimit(Integer countLimit) {
        this.countLimit = countLimit;
    }
}
