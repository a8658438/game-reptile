package com.tc.reptile.model;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 18:36 2019/4/13
 */
public class ArticleParam {
    /**
     * 文章来源ID
     */
    private Integer sourceId;
    /**
     * 开始日期
     */
    private Integer startDate;
    /**
     * 结束日期
     */
    private Integer endDate;
    /**
     * 文章标题
     */
    private String title;
    /**
     * 文章内容
     */
    private String content;
    /**
     * 文章分类
     */
    private String type;

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getStartDate() {
        return startDate;
    }

    public void setStartDate(Integer startDate) {
        this.startDate = startDate;
    }

    public Integer getEndDate() {
        return endDate;
    }

    public void setEndDate(Integer endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
