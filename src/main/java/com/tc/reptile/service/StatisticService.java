package com.tc.reptile.service;

import com.tc.reptile.constant.ArticleStatusEnum;
import com.tc.reptile.dao.ArticleInfoDao;
import com.tc.reptile.dao.GameAppearRecordDao;
import com.tc.reptile.dao.WebInfoDao;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.model.*;
import com.tc.reptile.util.DateUtil;
import com.tc.reptile.util.NumberUtil;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.NumberFormat;
import java.time.Instant;
import java.util.*;

@Service
public class StatisticService {
    private final WebInfoDao webInfoDao;
    private final ArticleInfoDao articleInfoDao;
    private final GameAppearRecordDao recordDao;

    public StatisticService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao, GameAppearRecordDao recordDao) {
        this.webInfoDao = webInfoDao;
        this.articleInfoDao = articleInfoDao;
        this.recordDao = recordDao;
    }

    public List<WebArticleHotDTO> currentHotRank() {
        List<WebArticleHotDTO> list = new ArrayList<>();

        webInfoDao.findAll().forEach(web -> {
            WebArticleHotDTO hot = new WebArticleHotDTO();
            hot.setWebName(web.getWebName());
            List<ArticleInfoEntity> articleList = articleInfoDao.findAllByStatusAndSourceIdOrderByHotDesc(ArticleStatusEnum.ALREADY.getStatus(), web.getId());
            hot.setArticleList(articleList);
            list.add(hot);
        });
        return list;
    }


    /***
     * @Author: Chensr
     * @Description: 游戏变化排行
     * @Date: 2019/4/17 21:15
     * @param
     * @return: java.lang.Object
     */
    public List<GameChangeDTO> changeRank() {
        DateTime current = new DateTime();
        long lastWeek = current.plusDays(-7).getMillis() / 1000;
        long previousWeek = current.plusDays(-14).getMillis() / 1000;

        // 统计本周游戏的数量
        List<GameChangeDTO> currentList = recordDao.gameCount(lastWeek, current.getMillis() / 1000);
        // 上周游戏数量
        List<GameChangeDTO> lastList = recordDao.gameCount(previousWeek, lastWeek);

        // 按游戏名称分类统计
        Map<String, Integer> lastCoutMap = new HashMap<>();
        lastList.forEach(game -> lastCoutMap.put(game.getGameName(), game.getTotal()));

        List<GameChangeDTO> countList = new ArrayList<>();
        Map<String, Integer> currentCoutMap = new HashMap<>();
        currentList.forEach(game -> {
            currentCoutMap.put(game.getGameName(), game.getTotal());

            // 如果原本存在，计算游戏变化值,不存在则是首次提起
            if (lastCoutMap.containsKey(game.getGameName()) || game.getTotal() != 1) {
                game.setChangeCount(lastCoutMap.containsKey(game.getGameName()) ? game.getTotal() - lastCoutMap.get(game.getGameName()) : game.getTotal());
                countList.add(game);
            }
        });

        // 判断上周出现过的游戏，在本周却没有再出现的情况
        lastList.forEach(game -> {
            if (!currentCoutMap.containsKey(game.getGameName()) && game.getTotal() != 1) {
                game.setChangeCount(0 - game.getTotal());
                game.setTotal(0);
                countList.add(game);
            }
        });

        // 对结果进行排序
        Collections.sort(countList);
        return countList;
    }

    /***
     * @Author: Chensr
     * @Description: 游戏数量统计
     * @Date: 2019/4/17 21:15
     * @param
     * @return: java.lang.Object
     */
    public List<GameCountDTO> gameCount() {
        return recordDao.gameCount();
    }

    /**
     * 多维度统计文章爬取数量变化情况
     *
     * @return
     */
    public ArticleChangeDTO articleChangRank() {
        DateTime currentTime = new DateTime();
        // 查询近3个月文章数据总量
        Integer total = articleInfoDao.countArticleByTimeRank(DateUtil.getDayStartSecond(currentTime.plusMonths(-3)), DateUtil.getDayEndSecond(currentTime));

        List<Map<String, Integer>> weekList = new ArrayList<>();
        List<Map<String, Integer>> dayList = new ArrayList<>();

        for (int i = 1; i <= 8; i++) {
            // 查询周的数据量
            Integer startWeek = DateUtil.getDayStartSecond(currentTime.plusDays(-7 * i));
            Integer endWeek = DateUtil.getDayEndSecond(currentTime.plusDays((1 - i) * 7));

            Integer weekCount = articleInfoDao.countArticleByTimeRank(startWeek, endWeek);
            Map<String, Integer> weekMap = new HashMap<>();
            weekMap.put("startTime", startWeek);
            weekMap.put("endTime", endWeek);
            weekMap.put("count", weekCount);
            weekList.add(weekMap);

            // 查询日的数据量
            Integer startDay = DateUtil.getDayStartSecond(currentTime.plusDays(1 - i));
            Integer endDay = DateUtil.getDayEndSecond(currentTime.plusDays(1 - i));

            Integer dayCount = articleInfoDao.countArticleByTimeRank(startDay, endDay);
            Map<String, Integer> dayMap = new HashMap<>();
            dayMap.put("date", startDay);
            dayMap.put("count", dayCount);
            dayList.add(dayMap);
        }

        // 封装响应数据
        ArticleChangeDTO dto = new ArticleChangeDTO();

        // 文章变化统计
        ArticleChangeDTO.ArticleRate rate = dto.new ArticleRate();
        rate.setTotal(total);

        Integer currentWeekCount = weekList.get(0).get("count");
        Integer preWeekCount = weekList.get(1).get("count");
        rate.setWeekUpOrDown(NumberUtil.equalsNum(currentWeekCount, preWeekCount));
        rate.setWeekPercent(NumberUtil.getPercent(Math.abs(currentWeekCount - preWeekCount), preWeekCount));

        Integer currentDayCount = dayList.get(0).get("count");
        Integer preDayCount = dayList.get(1).get("count");
        rate.setDayUpOrDown(NumberUtil.equalsNum(currentDayCount, preDayCount));
        rate.setDayPercent(NumberUtil.getPercent(Math.abs(currentDayCount - preDayCount), preDayCount));


        // 文章日变化统计
        ArticleChangeDTO.ArticleDayCount articleDayCount = dto.new ArticleDayCount();
        articleDayCount.setDayChangCount(currentDayCount - preDayCount);
        articleDayCount.setChangList(dayList);

        // 文章周变化统计
        ArticleChangeDTO.ArticleWeekCount articleWeekCount = dto.new ArticleWeekCount();
        articleWeekCount.setWeekChangCount(currentWeekCount - preWeekCount);
        articleWeekCount.setChangList(weekList);

        dto.setRate(rate);
        dto.setDayCount(articleDayCount);
        dto.setWeekCount(articleWeekCount);
        return dto;
    }

    /***
     * @Author: Chensr
     * @Description: 统计文章分类的文章数量
     * @Date: 2019/4/22 20:54
     * @param
     * @return: java.lang.Object
    */
    public  List<ArticleTypeCountDTO> articleTypeCount() {
        List<ArticleTypeCountDTO> list = new ArrayList<>();
        // 获取网站数量
        webInfoDao.findAll().forEach(web -> {
            List<Map<String, Object>> typeList = articleInfoDao.countArticleType(web.getId());

            ArticleTypeCountDTO dto = new ArticleTypeCountDTO();
            dto.setId(web.getId());
            dto.setWebName(web.getWebName());
            dto.setTypeList(typeList);

            list.add(dto);
        });
        return list;
    }
}
