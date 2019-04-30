package com.tc.reptile.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tc.reptile.config.ReptileProperties;
import com.tc.reptile.config.YingdiProperties;
import com.tc.reptile.constant.ArticleStatusEnum;
import com.tc.reptile.dao.*;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.entity.WebInfoEntity;
import com.tc.reptile.util.DateUtil;
import com.tc.reptile.util.HtmlUtil;
import com.tc.reptile.util.HttpUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 20:07 2019/4/30
 */
@Service
public class YingdiReptileService extends ReptileService {
    private Logger logger = LoggerFactory.getLogger(YingdiReptileService.class);
    private static final RestTemplate restTemplate = new RestTemplate();
    private final YingdiProperties yingdiProperties;

    public YingdiReptileService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao, ReptileProperties properties, GameAppearRecordDao recordDao, ArticleContentDao contentDao, ReptileRecordDao reptileRecordDao, ArticleTypeInfoDao articleTypeInfoDao, YingdiProperties yingdiProperties) {
        super(webInfoDao, articleInfoDao, properties, recordDao, contentDao, reptileRecordDao, articleTypeInfoDao);
        this.yingdiProperties = yingdiProperties;
    }

    private Integer lastId = null;

    @Override
    public void asyncReptileWeb(Integer currentSecond, WebInfoEntity webInfoEntity) {
        String articleUrl = webInfoEntity.getUrl();
        Map map = new HashMap();
        for (int x = 1; x <= 2; x++) {
            lastId = null;
            Integer seed = x == 1 ? 21 : 4, tagId = x == 1 ? 23 : 21;
            map.put("tagId", tagId);
            for (int i = 1; i < 999; i++) {
                map.put("page", i);
                webInfoEntity.setUrl(String.format(articleUrl, seed, tagId));
                logger.info("开始爬取网站:{},当前爬取页数:{}, 分类URL：{}", webInfoEntity.getWebName(), i, webInfoEntity.getUrl());

                boolean b = reptileArticleList(webInfoEntity, map);

                // 达到了停止爬取条件
                if (b) {
                    break;
                }
                threadSleep(2000);
            }
        }

        webInfoEntity.setUrl(articleUrl);
        // 爬取文章内容
        reptileArticleContent(webInfoEntity.getId());
        repticleComplete(currentSecond, webInfoEntity);
    }

    @Override
    public boolean reptileArticleList(WebInfoEntity webInfoEntity, Map<String, Object> param) {
        // 查询数据
        String url = (int) param.get("page") == 1 ? webInfoEntity.getUrl() : webInfoEntity.getUrl() + lastId;
        logger.info("旅法师营地分页查询的URL为：{}", url);
        String forObject = restTemplate.getForObject(url, String.class);
        JSONObject result = JSONObject.parseObject(forObject);
        if (!result.getBoolean("success")) {
            return false;
        }

        JSONArray articles = result.getJSONArray("feeds");
        int count = 0; //计数器统计是否达到停止爬取条件。因为有的不同分类有相同文章
        for (int i = 0; i < articles.size(); i++) {
            JSONObject article = ((JSONObject) articles.get(i)).getJSONObject("feed");
            // 记录最后的ID
            if (i == articles.size() - 1) {
                lastId = article.getInteger("id");
            }


            String href;
            if (article.getString("clazz").equals("bbsPost")) {
                href = yingdiProperties.getBbsPostUrl() + article.getString("sourceID");
            } else if ((int) param.get("tagId") == 21) {
                href = yingdiProperties.getArticleUrl() + article.getString("sourceID");
            } else {
                href = yingdiProperties.getArticleUrl2() + article.getString("sourceID");
            }

            Integer releaseTime = article.getInteger("created");

            // 判断是否达到停止爬取的条件
            if (stopReptile(webInfoEntity.getLastTime(), releaseTime, href)) {
                count++;
                continue;
            }

            ArticleInfoEntity articleInfoEntity = analysisArticle(href, releaseTime, webInfoEntity, article);
            articleInfoDao.save(articleInfoEntity);

            // 保存文章标签
            article.getJSONArray("rstagtitles").forEach(obj -> {
                JSONObject tag = (JSONObject) obj;
                saveArticleType(articleInfoEntity.getId(), webInfoEntity.getId(), tag.getString("tag"));
            });

        }

        return count == articles.size();
    }

    @Override
    public void reptileArticleContent(Long sourceId) {
        // 查询文章列表
        List<ArticleInfoEntity> articleList = articleInfoDao.findAllByStatusAndSourceId(ArticleStatusEnum.NOT_YET.getStatus(), sourceId);

        for (ArticleInfoEntity article : articleList) {
            logger.info("爬取文章内容，文章ID：{},文章标题：{}", article.getId(), article.getTitle());
            String sourceArticleId = article.getUrl().replace(yingdiProperties.getBbsPostUrl(), "").replace(yingdiProperties.getArticleUrl(), "").replace(yingdiProperties.getArticleUrl2(), "");
            String url = article.getUrl().contains("bbspost") ? yingdiProperties.getBbsArticleUrl() + sourceArticleId : yingdiProperties.getArticleDetileUrl() + sourceArticleId;
            String result = restTemplate.getForObject(url, String.class);
            if (StringUtils.isEmpty(result)) {
                continue;
            }
            JSONObject content = JSONObject.parseObject(result);

            String html = null;
            if (article.getUrl().contains("bbspost")) {
                html = content.getJSONObject("bbsPost").getJSONObject("bbsPost").getString("content");
            } else {
                StringBuilder srt = new StringBuilder();
                content.getJSONObject("article").getJSONArray("content").forEach(obj -> {
                    JSONObject o = (JSONObject) obj;
                    if (o.getString("type").equals("text")) {
                        srt.append(o.getString("content"));
                    } else if (o.getString("type").equals("image")) {
                        srt.append("<img src=\"" + o.getString("url") + "\"></img>");
                    }
                });
                html = srt.toString();
            }
            saveGameData(article, html);
            saveArticleContent(article, html);
            updateArticle(article, html);

            threadSleep(2000);
        }
    }

    @Override
    public void updateArticle(ArticleInfoEntity articleInfoEntity, Document document) {

    }

    public void updateArticle(ArticleInfoEntity articleInfoEntity, String html) {
        articleInfoEntity.setStatus(ArticleStatusEnum.ALREADY.getStatus());
        articleInfoEntity.setContentBreviary(HtmlUtil.getBreviary(html));
        articleInfoDao.save(articleInfoEntity);
    }

    @Override
    protected ArticleInfoEntity analysisArticle(String articleUrl, Integer releaseTime, WebInfoEntity webInfoEntity, Object articleObj) {
        JSONObject article = (JSONObject) articleObj;

        ArticleInfoEntity articleInfo = new ArticleInfoEntity();
        articleInfo.setCreateTime(DateUtil.getCurrentSecond());
        articleInfo.setReleaseTime(releaseTime);
        articleInfo.setSourceId(webInfoEntity.getId());
        articleInfo.setSource(webInfoEntity.getWebName());
        articleInfo.setTitle(article.getString("title"));
        articleInfo.setUrl(articleUrl);
        articleInfo.setStatus(ArticleStatusEnum.NOT_YET.getStatus());
        articleInfo.setImageUrl(article.getString("icon"));
        articleInfo.setAuthor(article.getString("username"));
        articleInfo.setHot(article.getString("clazz").equals("bbsPost") ? ((JSONObject) (article.getJSONArray("data").get(0))).getInteger("reply") : article.getInteger("reply"));
        return articleInfo;
    }

}
