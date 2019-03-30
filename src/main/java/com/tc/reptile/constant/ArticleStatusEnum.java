package com.tc.reptile.constant;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 16:46 2019/3/30
 */
public enum ArticleStatusEnum {
    /**
     * 未爬
     */
    NOT_YET(0),
    /**
     * 已爬
     */
    ALREADY(1);
    private Integer status;

    ArticleStatusEnum(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }}
