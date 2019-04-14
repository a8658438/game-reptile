package com.tc.reptile.model;

import com.tc.reptile.entity.ArticleInfoEntity;

import java.util.List;

public class WebArticleHotDTO {
    /**
     * 站点名称
     */
    private String webName;

    private List<ArticleInfoEntity> articleList;

    public String getWebName() {
        return webName;
    }

    public void setWebName(String webName) {
        this.webName = webName;
    }

    public List<ArticleInfoEntity> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<ArticleInfoEntity> articleList) {
        this.articleList = articleList;
    }
}
