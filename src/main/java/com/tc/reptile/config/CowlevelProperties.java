package com.tc.reptile.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 18:24 2019/4/24
 */
@ConfigurationProperties(prefix = "config.cowlevel")
public class CowlevelProperties {
    private String account;
    private String password;
    private String loginUrl;

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
