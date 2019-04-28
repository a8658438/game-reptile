package com.tc.reptile.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(value = "config.vgtime")
public class VgTimeProperties {
    private List<String> typeUrls;

    public List<String> getTypeUrls() {
        return typeUrls;
    }

    public void setTypeUrls(List<String> typeUrls) {
        this.typeUrls = typeUrls;
    }
}
