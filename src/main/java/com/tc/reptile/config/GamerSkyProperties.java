package com.tc.reptile.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 20:09 2019/4/29
 */
@ConfigurationProperties(value = "config.gamesky")
public class GamerSkyProperties {
    private String hotUrl;

    public String getHotUrl() {
        return hotUrl;
    }

    public void setHotUrl(String hotUrl) {
        this.hotUrl = hotUrl;
    }
}
