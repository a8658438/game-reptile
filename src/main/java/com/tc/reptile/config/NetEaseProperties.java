package com.tc.reptile.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 14:38 2019/4/29
 */
@ConfigurationProperties(value = "config.netease")
public class NetEaseProperties {
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
