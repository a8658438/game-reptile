package com.tc.reptile.model;

import java.util.List;
import java.util.Map;

public class ArticleChangeDTO {
    /**
     * 同比
     */
    private ArticleRate rate;
    /**
     * 日数量变化统计
     */
    private ArticleDayCount dayCount;
    /**
     * 周变化数量统计
     */
    private ArticleWeekCount weekCount;

    public ArticleRate getRate() {
        return rate;
    }

    public void setRate(ArticleRate rate) {
        this.rate = rate;
    }

    public ArticleDayCount getDayCount() {
        return dayCount;
    }

    public void setDayCount(ArticleDayCount dayCount) {
        this.dayCount = dayCount;
    }

    public ArticleWeekCount getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(ArticleWeekCount weekCount) {
        this.weekCount = weekCount;
    }

    public class ArticleRate {
        /**
         * 文章总数
         */
        private Integer total;
        /**
         * 周百分比
         */
        private String weekPercent;
        /**
         * 周涨还是跌
         */
        private Integer weekUpOrDown;
        /**
         * 日百分比
         */
        private String dayPercent;
        /**
         * 日涨还是跌 1 涨 0平 -1跌
         */
        private Integer dayUpOrDown;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public String getWeekPercent() {
            return weekPercent;
        }

        public void setWeekPercent(String weekPercent) {
            this.weekPercent = weekPercent;
        }

        public Integer getWeekUpOrDown() {
            return weekUpOrDown;
        }

        public void setWeekUpOrDown(Integer weekUpOrDown) {
            this.weekUpOrDown = weekUpOrDown;
        }

        public String getDayPercent() {
            return dayPercent;
        }

        public void setDayPercent(String dayPercent) {
            this.dayPercent = dayPercent;
        }

        public Integer getDayUpOrDown() {
            return dayUpOrDown;
        }

        public void setDayUpOrDown(Integer dayUpOrDown) {
            this.dayUpOrDown = dayUpOrDown;
        }
    }

    public class ArticleDayCount {
        private Integer dayChangCount;
        private List<Map<String, Integer>> changList;

        public Integer getDayChangCount() {
            return dayChangCount;
        }

        public void setDayChangCount(Integer dayChangCount) {
            this.dayChangCount = dayChangCount;
        }

        public List<Map<String, Integer>> getChangList() {
            return changList;
        }

        public void setChangList(List<Map<String, Integer>> changList) {
            this.changList = changList;
        }
    }

    public class ArticleWeekCount {
        private Integer weekChangCount;
        private List<Map<String, Integer>> changList;

        public Integer getWeekChangCount() {
            return weekChangCount;
        }

        public void setWeekChangCount(Integer weekChangCount) {
            this.weekChangCount = weekChangCount;
        }

        public List<Map<String, Integer>> getChangList() {
            return changList;
        }

        public void setChangList(List<Map<String, Integer>> changList) {
            this.changList = changList;
        }
    }

}