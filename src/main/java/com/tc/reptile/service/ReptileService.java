package com.tc.reptile.service;

import com.tc.reptile.config.ReptileProperties;
import com.tc.reptile.dao.*;
import com.tc.reptile.entity.*;
import com.tc.reptile.util.DateUtil;
import com.tc.reptile.util.RegexUtil;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    protected final ArticleTypeInfoDao articleTypeInfoDao;


    public ReptileService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao, ReptileProperties properties, GameAppearRecordDao recordDao, ArticleContentDao contentDao, ReptileRecordDao reptileRecordDao, ArticleTypeInfoDao articleTypeInfoDao) {
        this.webInfoDao = webInfoDao;
        this.articleInfoDao = articleInfoDao;
        this.properties = properties;
        this.recordDao = recordDao;
        this.contentDao = contentDao;
        this.reptileRecordDao = reptileRecordDao;
        this.articleTypeInfoDao = articleTypeInfoDao;
    }

    /***
     * @Author: Chensr
     * @Description: 爬取网站数据
     * @Date: 2019/3/29 21:02
     * @param
     * @return: void
     */
    @Transactional
    public abstract boolean reptileArticleList(WebInfoEntity webInfoEntity, Map<String, Object> param);

    /***
     * @Author: Chensr
     * @Description: 爬取文章内容数据
     * @Date: 2019/3/30 14:32
     * @param
     * @return: void
     */
    @Transactional
    public abstract void reptileArticleContent(Long sourceId);

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
        content.setContent(html);
        content.setArticleId(articleInfoEntity.getId());
        contentDao.save(content);
    }

    /***
     * @Author: Chensr
     * @Description: 补充更新文章信息
     * @Date: 2019/4/26 11:36
     * @param articleInfoEntity
     * @param document
     * @return: void
     */
    @Transactional
    public abstract void updateArticle(ArticleInfoEntity articleInfoEntity, Document document);

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
    public void asyncReptileWeb(Integer currentSecond, WebInfoEntity webInfoEntity, Integer isAuto){
        Map<String, Object> param = new HashMap<>();
        for (int i = 1; i < 999; i++) {
            logger.info("开始爬取网站:{},当前爬取页数:{}", webInfoEntity.getWebName(), i);
            param.put("page", i);
            boolean b = reptileArticleList(webInfoEntity, param);

            // 达到了停止爬取条件
            if (b) {
                break;
            }
            threadSleep(2000);
        }

        // 爬取文章内容
        reptileArticleContent(webInfoEntity.getId());
        repticleComplete(currentSecond, webInfoEntity, isAuto);
    }

    /**
     * 线程睡眠时间
     *
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
     *  @param currentSecond
     * @param webInfoEntity
     * @param isAuto
     */
    @Transactional
    public void repticleComplete(Integer currentSecond, WebInfoEntity webInfoEntity, Integer isAuto) {
        // 更新网站信息
        webInfoEntity.setLastTime(DateUtil.getCurrentSecond());
        if (isAuto.equals(1)) {
            webInfoEntity.setAutoReptileCount(webInfoEntity.getAutoReptileCount() + 1);
        } else {
            webInfoEntity.setReptileCount(webInfoEntity.getReptileCount() + 1);
        }
        webInfoDao.save(webInfoEntity);

        // 更新爬取记录信息
        reptileRecordDao.updateRecord(DateUtil.getCurrentSecond(), currentSecond);
    }

    /**
     * 保存文章与分类的关系
     * @param articleId
     * @param sourceId
     * @param type
     */
    public void saveArticleType(Long articleId, Long sourceId, String type) {
        // 保存与分类的关系
        ArticleTypeInfoEntity articleType = new ArticleTypeInfoEntity();
        articleType.setArticleId(articleId);
        articleType.setSourceId(sourceId);
        articleType.setTypeName(type);
        articleType.setCreateTime(DateUtil.getCurrentSecond());
        articleTypeInfoDao.save(articleType);
    }

    /**
     * 解析文章对象
     *
     * @param articleUrl
     * @param releaseTime
     * @param webInfoEntity
     * @param articleObj
     * @return
     */
    protected abstract ArticleInfoEntity analysisArticle(String articleUrl, Integer releaseTime, WebInfoEntity webInfoEntity, Object articleObj);
}
