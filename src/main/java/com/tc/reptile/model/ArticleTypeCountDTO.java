package com.tc.reptile.model;

import java.util.List;
import java.util.Map;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 21:02 2019/4/22
 */
public class ArticleTypeCountDTO {
    /**
     * 网站ID
     */
    private Long id;
    /**
     * 网站名称
     */
    private String webName;
    /**
     * 分类数量统计
     */
    private List<Map<String, Object>> typeList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWebName() {
        return webName;
    }

    public void setWebName(String webName) {
        this.webName = webName;
    }

    public List<Map<String, Object>> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<Map<String, Object>> typeList) {
        this.typeList = typeList;
    }
}
