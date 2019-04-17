package com.tc.reptile.model;

public class GameCountDTO {
    /**
     * 游戏名称
     */
    private String gameName;
    /**
     * 游戏统计
     */
    private Integer total;


    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
