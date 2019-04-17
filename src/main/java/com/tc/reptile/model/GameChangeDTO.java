package com.tc.reptile.model;

public class GameChangeDTO extends GameCountDTO implements Comparable<GameChangeDTO>{

    /**
     * 游戏变化统计
     */
    private Integer changeCount;

    public Integer getChangeCount() {
        return changeCount;
    }

    public void setChangeCount(Integer changeCount) {
        this.changeCount = changeCount;
    }

    @Override
    public int compareTo(GameChangeDTO ohter) {
        return Math.abs(ohter.getChangeCount()) - Math.abs(this.getChangeCount());
    }
}
