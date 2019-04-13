package com.tc.reptile.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tc.reptile.config.ReptileProperties;
import com.tc.reptile.constant.ArticleStatusEnum;
import com.tc.reptile.constant.YystvConstant;
import com.tc.reptile.dao.ArticleContentDao;
import com.tc.reptile.dao.ArticleInfoDao;
import com.tc.reptile.dao.GameAppearRecordDao;
import com.tc.reptile.dao.WebInfoDao;
import com.tc.reptile.entity.ArticleContentEntity;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.entity.GameAppearRecordEntity;
import com.tc.reptile.entity.WebInfoEntity;
import com.tc.reptile.util.DateUtil;
import com.tc.reptile.util.HttpUtil;
import com.tc.reptile.util.RegexUtil;
import com.vdurmont.emoji.EmojiParser;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 20:54 2019/3/29
 */
@Service
public class ReptileService {
    private Logger logger = LoggerFactory.getLogger(ReptileService.class);

    private final WebInfoDao webInfoDao;
    private final ArticleInfoDao articleInfoDao;
    private final ReptileProperties properties;
    private final GameAppearRecordDao recordDao;
    private final ArticleContentDao contentDao;

    public ReptileService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao, ReptileProperties properties, GameAppearRecordDao recordDao, ArticleContentDao contentDao) {
        this.webInfoDao = webInfoDao;
        this.articleInfoDao = articleInfoDao;
        this.properties = properties;
        this.recordDao = recordDao;
        this.contentDao = contentDao;
    }

    /***
     * @Author: Chensr
     * @Description: 爬取网站数据
     * @Date: 2019/3/29 21:02
     * @param
     * @return: void
     */
    @Transactional
    public boolean reptileArticleList(WebInfoEntity webInfoEntity, Map<String, Object> param) {
        List<ArticleInfoEntity> list = new ArrayList<>();
        boolean flag = true;
        // 查询数据
        Optional<JSONArray> data = HttpUtil.getDataForJson(webInfoEntity.getUrl(), param);
        if (!data.isPresent()) {
            return false;
        }
        JSONArray array = data.get();
        for (Object o : array) {

            JSONObject article = (JSONObject) o;
            // 查询文章是否存在或者 是否是过旧的数据
            String articleUrl = webInfoEntity.getArticleUrl() + article.get(YystvConstant.ARTICLE_ID);
            Integer releaseTime = DateUtil.getDateSecond((String) article.get(YystvConstant.ARTICLE_CREATETIME), DateUtil.FORMAT_TYPE_1);

            // 判断是否达到停止爬取的条件
            if (stopReptile(webInfoEntity.getLastTime(), releaseTime, articleUrl)) {
                flag = false;
                break;
            }

            ArticleInfoEntity articleInfo = new ArticleInfoEntity();
            articleInfo.setAuthor((String) article.get(YystvConstant.ARTICLE_AUTHOR));
            articleInfo.setCreateTime(DateUtil.getCurrentSecond());
            articleInfo.setReleaseTime(releaseTime);
            articleInfo.setSourceId(webInfoEntity.getId());
            articleInfo.setSource(webInfoEntity.getWebName());
            articleInfo.setTitle((String) article.get(YystvConstant.ARTICLE_TITLE));
            articleInfo.setUrl(articleUrl);
            articleInfo.setStatus(ArticleStatusEnum.NOT_YET.getStatus());
            list.add(articleInfo);
        }

        articleInfoDao.saveAll(list);
        return flag;
    }

    /***
     * @Author: Chensr
     * @Description: 爬取文章内容数据
     * @Date: 2019/3/30 14:32
     * @param
     * @return: void
     */
    public void reptileArticleContent() {
        // 查询文章列表
        List<ArticleInfoEntity> articleList = articleInfoDao.findAllByStatus(ArticleStatusEnum.NOT_YET.getStatus());
        for (ArticleInfoEntity article : articleList) {
            try {
                saveGameData(article, HttpUtil.getDocument(article.getUrl(), "http://www.yystv.cn/"));

                // 睡眠2秒，防止被网站拉进黑名单
                Thread.sleep(2000);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

    /***
     * @Author: Chensr
     * @Description: 保存文章相关的游戏数据
     * @Date: 2019/3/30 16:55
     * @param articleInfoEntity
     * @param html
     * @return: void
     */
    @Transactional
    public void saveGameData(ArticleInfoEntity articleInfoEntity, Document document) {
        String html = document.html();
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

        // 保存文章内容
        ArticleContentEntity content = new ArticleContentEntity();
        content.setContent(html); // 过滤掉emoji表情，防止报错
        contentDao.save(content);

        // 更新文章状态和点赞次数
        Elements tags = document.getElementsByClass(YystvConstant.HOT_CLASS);
        if (!tags.isEmpty()) {
            String count = tags.get(0).html();
            articleInfoEntity.setHot(StringUtils.isEmpty(count) ? 0 : Integer.parseInt(count));
        }
        articleInfoEntity.setStatus(ArticleStatusEnum.ALREADY.getStatus());
        articleInfoDao.save(articleInfoEntity);
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
    private boolean stopReptile(Integer reptileLastTime, Integer releaseTime, String articleUrl) {
        Optional<ArticleInfoEntity> infoEntity = articleInfoDao.findByUrl(articleUrl);
        return infoEntity.isPresent() || (reptileLastTime != null && releaseTime < reptileLastTime) || releaseTime < properties.getReadTime();
    }

}
