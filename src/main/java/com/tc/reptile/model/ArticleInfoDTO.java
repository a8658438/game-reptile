package com.tc.reptile.model;

import javax.persistence.Column;
import java.util.List;

public class ArticleInfoDTO {
    private Long id;

    /**
     * 来源
     */
    private Long sourceId;

    /**
     * 来源
     */
    private String source;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章链接
     */
    private String url;

    /**
     * 文章缩略图
     */
    private String imageUrl;
    /**
     * 文章内容缩略
     */
    private String contentBreviary;
    /**
     * 文章分类
     */
    private List<String> typeList;

    /**
     * 发布时间
     */
    private Integer releaseTime;

    /**
     * 作者
     */
    private String author;

    /**
     * 热度
     */
    private Integer hot;

    /**
     * 创建时间
     */
    private Integer createTime;
    /**
     * 关联游戏
     */
    private String includeGames;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContentBreviary() {
        return contentBreviary;
    }

    public void setContentBreviary(String contentBreviary) {
        this.contentBreviary = contentBreviary;
    }

    public List<String> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
    }

    public Integer getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Integer releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public String getIncludeGames() {
        return includeGames;
    }

    public void setIncludeGames(String includeGames) {
        this.includeGames = includeGames;
    }
}
