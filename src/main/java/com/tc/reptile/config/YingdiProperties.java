package com.tc.reptile.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 22:20 2019/4/30
 */
@ConfigurationProperties(value = "config.yingdi")
public class YingdiProperties {
    private String bbsPostUrl;
    private String articleUrl;
    private String articleUrl2;
    private String bbsArticleUrl;
    private String articleDetileUrl;

    public String getBbsArticleUrl() {
        return bbsArticleUrl;
    }

    public void setBbsArticleUrl(String bbsArticleUrl) {
        this.bbsArticleUrl = bbsArticleUrl;
    }

    public String getArticleDetileUrl() {
        return articleDetileUrl;
    }

    public void setArticleDetileUrl(String articleDetileUrl) {
        this.articleDetileUrl = articleDetileUrl;
    }

    public String getBbsPostUrl() {
        return bbsPostUrl;
    }

    public void setBbsPostUrl(String bbsPostUrl) {
        this.bbsPostUrl = bbsPostUrl;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getArticleUrl2() {
        return articleUrl2;
    }

    public void setArticleUrl2(String articleUrl2) {
        this.articleUrl2 = articleUrl2;
    }
}
