package com.tc.reptile.service;

import com.alibaba.fastjson.JSONObject;
import com.tc.reptile.config.ReptileProperties;
import com.tc.reptile.dao.*;
import com.tc.reptile.entity.ArticleContentEntity;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.entity.GameAppearRecordEntity;
import com.tc.reptile.entity.WebInfoEntity;
import com.tc.reptile.util.DateUtil;
import com.tc.reptile.util.RegexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 20:54 2019/3/29
 */
public abstract class ReptileService {
    private Logger logger = LoggerFactory.getLogger(ReptileService.class);

    protected final WebInfoDao webInfoDao;
    protected final ArticleInfoDao articleInfoDao;
    protected final ReptileProperties properties;
    protected final GameAppearRecordDao recordDao;
    protected final ArticleContentDao contentDao;
    protected final ReptileRecordDao reptileRecordDao;

    public ReptileService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao, ReptileProperties properties, GameAppearRecordDao recordDao, ArticleContentDao contentDao, ReptileRecordDao reptileRecordDao) {
        this.webInfoDao = webInfoDao;
        this.articleInfoDao = articleInfoDao;
        this.properties = properties;
        this.recordDao = recordDao;
        this.contentDao = contentDao;
        this.reptileRecordDao = reptileRecordDao;
    }

    /***
     * @Author: Chensr
     * @Description: 爬取网站数据
     * @Date: 2019/3/29 21:02
     * @param
     * @return: void
     */
    public abstract boolean reptileArticleList(WebInfoEntity webInfoEntity, Map<String, Object> param);

    /***
     * @Author: Chensr
     * @Description: 爬取文章内容数据
     * @Date: 2019/3/30 14:32
     * @param
     * @return: void
     */
    public abstract void reptileArticleContent();

    /***
     * @Author: Chensr
     * @Description: 保存文章相关的游戏数据
     * @Date: 2019/3/30 16:55
     * @param articleInfoEntity
     * @param html
     * @return: void
     */
    @Transactional
    public void saveGameData(ArticleInfoEntity articleInfoEntity, String html) {
        // 保存文章提到的游戏
        List<GameAppearRecordEntity> recordList = new ArrayList<>();
        RegexUtil.getGames(html).forEach(game -> {
            GameAppearRecordEntity record = new GameAppearRecordEntity();
            record.setArticleId(articleInfoEntity.getId());
            record.setReleaseTime(articleInfoEntity.getReleaseTime());
            record.setGameName(game);
            recordList.add(record);
        });
        recordDao.saveAll(recordList);
    }

    @Transactional
    public void saveArticleContent(ArticleInfoEntity articleInfoEntity, String html) {
        // 保存文章内容
        ArticleContentEntity content = new ArticleContentEntity();
        content.setContent(html); // 过滤掉emoji表情，防止报错
        content.setArticleId(articleInfoEntity.getId());
        contentDao.save(content);
    }
    /***
     * @Author: Chensr
     * @Description: 出现重复的或制定时间的，停止爬取，更新网站爬取时间
     * @Date: 2019/3/30 14:17
     * @param reptileLastTime
     * @param releaseTime
     * @param articleUrl
     * @return: boolean
     */
    protected boolean stopReptile(Integer reptileLastTime, Integer releaseTime, String articleUrl) {
        Optional<ArticleInfoEntity> infoEntity = articleInfoDao.findByUrl(articleUrl);
        return infoEntity.isPresent() || (reptileLastTime != null && releaseTime < reptileLastTime) || releaseTime < properties.getReadTime();
    }


    @Async
    public abstract void asyncReptileWeb(Integer currentSecond, WebInfoEntity webInfoEntity);

    /**
     * 线程睡眠时间
     * @param millis
     */
    protected void threadSleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 爬取结束，更新爬取记录
     * @param currentSecond
     * @param webInfoEntity
     */
    @Transactional
    public void repticleComplete(Integer currentSecond, WebInfoEntity webInfoEntity) {
        // 更新网站信息
        webInfoEntity.setLastTime(DateUtil.getCurrentSecond());
        webInfoEntity.setReptileCount(webInfoEntity.getReptileCount() + 1);
        webInfoDao.save(webInfoEntity);

        // 更新爬取记录信息
        reptileRecordDao.updateRecord(DateUtil.getCurrentSecond(),currentSecond);
    }


    /**
     * 解析文章对象
     * @param articleUrl
     * @param releaseTime
     * @param webInfoEntity
     * @param article
     * @param type
     * @return
     */
    protected abstract ArticleInfoEntity analysisArticle( String articleUrl,Integer releaseTime, WebInfoEntity webInfoEntity, JSONObject article,String type);
}
