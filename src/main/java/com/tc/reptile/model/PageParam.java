package com.tc.reptile.model;

import java.io.Serializable;

/**
 * @author loocao
 * @date 2018-07-18
 */
public class PageParam implements Serializable {

    private Integer page = 1;
    private Integer pageSize = 10;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "PageParam{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
