package com.tc.reptile.service;

import com.tc.reptile.constant.ArticleStatusEnum;
import com.tc.reptile.dao.ArticleInfoDao;
import com.tc.reptile.dao.GameAppearRecordDao;
import com.tc.reptile.dao.WebInfoDao;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.model.GameChangeDTO;
import com.tc.reptile.model.GameCountDTO;
import com.tc.reptile.model.WebArticleHotDTO;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
                game.setChangeCount(lastCoutMap.containsKey(game.getGameName()) ? game.getTotal() - lastCoutMap.get(game.getGameName()): game.getTotal());
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
}
