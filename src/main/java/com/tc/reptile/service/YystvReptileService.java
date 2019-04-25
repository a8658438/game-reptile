package com.tc.reptile.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tc.reptile.config.ReptileProperties;
import com.tc.reptile.constant.ArticleStatusEnum;
import com.tc.reptile.constant.YystvConstant;
import com.tc.reptile.dao.*;
import com.tc.reptile.entity.*;
import com.tc.reptile.util.DateUtil;
import com.tc.reptile.util.HtmlUtil;
import com.tc.reptile.util.HttpUtil;
import com.tc.reptile.util.RegexUtil;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class YystvReptileService extends ReptileService {
    private Logger logger = LoggerFactory.getLogger(ReptileService.class);

    public YystvReptileService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao, ReptileProperties properties, GameAppearRecordDao recordDao, ArticleContentDao contentDao, ReptileRecordDao reptileRecordDao) {
        super(webInfoDao, articleInfoDao, properties, recordDao, contentDao, reptileRecordDao);
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
        // 查询数据
        Optional<JSONArray> data = HttpUtil.getDataForJson(webInfoEntity.getUrl(), param);
        if (!data.isPresent()) {
            return false;
        }
        JSONArray array = data.get();

        int count = 0; //计数器统计是否达到停止爬取条件。因为有的不同分类有相同文章
        for (Object o : array) {

            JSONObject article = (JSONObject) o;
            // 查询文章是否存在或者 是否是过旧的数据
            String articleUrl = webInfoEntity.getArticleUrl() + article.get(YystvConstant.ARTICLE_ID);
            Integer releaseTime = DateUtil.getDateSecond(article.getString(YystvConstant.ARTICLE_CREATETIME), DateUtil.FORMAT_TYPE_1);

            // 判断是否达到停止爬取的条件
            if (stopReptile(webInfoEntity.getLastTime(), releaseTime, articleUrl)) {
                count++;
                continue;
            }

            list.add(analysisArticle(articleUrl, releaseTime, webInfoEntity, article, null));
        }

        articleInfoDao.saveAll(list);
        return count == array.size();
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
            logger.info("爬去文章内容，文章ID：{}", article.getId());
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
        String html = document.getElementsByClass(YystvConstant.ARTICLE_CONTENT).get(0).child(0).html();
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
        content.setArticleId(articleInfoEntity.getId());
        contentDao.save(content);

        // 更新文章状态和点赞次数
        Elements tags = document.getElementsByClass(YystvConstant.HOT_CLASS);
        if (!tags.isEmpty()) {
            String count = tags.get(0).html();
            articleInfoEntity.setHot(StringUtils.isEmpty(count) ? 0 : Integer.parseInt(count));
        }
        String type = document.getElementsByClass(YystvConstant.ARTICLE_TYPE).get(0).text();

        articleInfoEntity.setType(type);
        articleInfoEntity.setContentBreviary(HtmlUtil.getBreviary(html));
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

    /**
     * 开始数据爬取工作
     *
     * @param size
     * @return
     */
    public Integer saveReptileRecord(Integer size) {
        // 生成爬取记录
        Integer currentSecond = DateUtil.getCurrentSecond();
        ReptileRecordEntity record = new ReptileRecordEntity();
        record.setId(currentSecond);
        record.setReptileCount(size);
        record.setFinishCount(0);
        record.setReptileTime(currentSecond);
        reptileRecordDao.save(record);
        return currentSecond;
    }

    @Async
    @Transactional
    public void asyncReptileWeb(Integer currentSecond, WebInfoEntity webInfoEntity) {
        Map<String, Object> param = new HashMap<>();
        for (int i = 0; i < 999; i++) {

            logger.info("开始爬取网站:{},当前爬取页数:{}", webInfoEntity.getWebName(), i);
            param.put("page", i);
            boolean b = reptileArticleList(webInfoEntity, param);

            // 达到了停止爬取条件
            if (b) {
                // 爬取文章内容
                this.reptileArticleContent();

                // 更新网站信息
                webInfoEntity.setLastTime(DateUtil.getCurrentSecond());
                webInfoEntity.setReptileCount(webInfoEntity.getReptileCount() + 1);
                webInfoDao.save(webInfoEntity);

                // 更新爬取记录信息
                reptileRecordDao.updateRecord(DateUtil.getCurrentSecond(), currentSecond);
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    protected ArticleInfoEntity analysisArticle(String articleUrl, Integer releaseTime, WebInfoEntity webInfoEntity, JSONObject article, String type) {
        ArticleInfoEntity articleInfo = new ArticleInfoEntity();
        articleInfo.setAuthor(article.getString(YystvConstant.ARTICLE_AUTHOR));
        articleInfo.setCreateTime(DateUtil.getCurrentSecond());
        articleInfo.setReleaseTime(releaseTime);
        articleInfo.setSourceId(webInfoEntity.getId());
        articleInfo.setSource(webInfoEntity.getWebName());
        articleInfo.setTitle(article.getString(YystvConstant.ARTICLE_TITLE));
        articleInfo.setUrl(articleUrl);
        articleInfo.setStatus(ArticleStatusEnum.NOT_YET.getStatus());
        articleInfo.setImageUrl(article.getString(YystvConstant.IMAGE_URL));
        return articleInfo;
    }

}
