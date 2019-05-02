package com.tc.reptile.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tc.reptile.config.ReptileProperties;
import com.tc.reptile.config.VgTimeProperties;
import com.tc.reptile.constant.ArticleStatusEnum;
import com.tc.reptile.constant.YystvConstant;
import com.tc.reptile.dao.*;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.entity.WebInfoEntity;
import com.tc.reptile.util.DateUtil;
import com.tc.reptile.util.HtmlUtil;
import com.tc.reptile.util.HttpUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class VgTimeReptileService extends ReptileService {
    private Logger logger = LoggerFactory.getLogger(ChuAppReptileService.class);
    private final VgTimeProperties vgProperties;
    private static final RestTemplate restTemplate = new RestTemplate();

    public VgTimeReptileService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao, ReptileProperties properties, GameAppearRecordDao recordDao, ArticleContentDao contentDao, ReptileRecordDao reptileRecordDao, ArticleTypeInfoDao articleTypeInfoDao, VgTimeProperties vgProperties) {
        super(webInfoDao, articleInfoDao, properties, recordDao, contentDao, reptileRecordDao, articleTypeInfoDao);
        this.vgProperties = vgProperties;
    }

    @Override
    public void asyncReptileWeb(Integer currentSecond, WebInfoEntity webInfoEntity) {
        vgProperties.getTypeUrls().forEach(url -> {
            for (int i = 1; i < 999; i++) {
                logger.info("开始爬取网站:{},当前爬取页数:{}, 分类URL：{}", webInfoEntity.getWebName(), i, url);

                webInfoEntity.setUrl(String.format(url, i));
                boolean b = reptileArticleList(webInfoEntity, null);

                // 达到了停止爬取条件
                if (b) {
                    break;
                }
                threadSleep(2000);
            }
        });

        // 爬取文章内容
        reptileArticleContent(webInfoEntity.getId());
        repticleComplete(currentSecond, webInfoEntity);
    }

    @Override
    public boolean reptileArticleList(WebInfoEntity webInfoEntity, Map<String, Object> param) {
        // 查询数据
        JSONObject jsonObject = restTemplate.getForObject(webInfoEntity.getUrl(), JSONObject.class);
        if (!jsonObject.getInteger("retcode").equals(200)) {
            return false;
        }

        JSONArray array = null;
        Elements elements = null;
        try {
            array = jsonObject.getJSONArray("data");
        } catch (Exception e) {
            elements =  new Document("").html(jsonObject.getString("data")).children();
        }


        int count = 0; //计数器统计是否达到停止爬取条件。因为有的不同分类有相同文章
        if (array != null) {
            String type = jsonObject.getString("name");
            for (Object o : array) {

                JSONObject article = (JSONObject) o;
                // 查询文章是否存在或者 是否是过旧的数据

                String url = String.format("/topic/%s.jhtml", StringUtils.isEmpty(article.get("topicId")) ? article.get("id") : article.get("topicId"));
                String articleUrl = webInfoEntity.getArticleUrl() + url;
                Integer releaseTime = article.getInteger("createTime");

                // 判断是否达到停止爬取的条件
                if (stopReptile(webInfoEntity.getLastTime(), releaseTime, articleUrl)) {
                    count++;
                    continue;
                }

                ArticleInfoEntity articleInfo = analysisArticle(articleUrl, releaseTime, webInfoEntity, article);
                articleInfoDao.save(articleInfo);

                // 保存分类
                saveArticleType(articleInfo.getId(), webInfoEntity.getId(), StringUtils.isEmpty(type) ? article.getString("typeTagName") : type);
            }
        } else {
            String type = "新闻";

            for (Element article : elements) {

                // 查询文章是否存在或者 是否是过旧的数据
                String articleUrl = webInfoEntity.getArticleUrl() + article.getElementsByTag("a").get(0).attr("href");
                Integer releaseTime = DateUtil.getDateSecond(article.getElementsByClass("time_box").get(0).text(), DateUtil.FORMAT_TYPE_1);

                // 判断是否达到停止爬取的条件
                if (stopReptile(webInfoEntity.getLastTime(), releaseTime, articleUrl)) {
                    count++;
                    continue;
                }

                ArticleInfoEntity articleInfo = analysisArticle(articleUrl, releaseTime, webInfoEntity, article);
                articleInfoDao.save(articleInfo);

                // 保存分类
                saveArticleType(articleInfo.getId(), webInfoEntity.getId(), type);
            }
        }

        return count == (array != null ? array.size() : elements.size());
    }

    @Override
    public void reptileArticleContent(Long sourceId) {
        // 查询文章列表
        List<ArticleInfoEntity> articleList = articleInfoDao.findAllByStatusAndSourceId(ArticleStatusEnum.NOT_YET.getStatus(), sourceId);

        a: for (ArticleInfoEntity article : articleList) {
            logger.info("爬取文章内容，文章ID：{}", article.getId());
            Document document = HttpUtil.getDocument(article.getUrl());
            if (document == null) { // 出现超时的情况，留到下次爬取
                continue;
            }

            // 获取分页数据
            Elements page = document.getElementsByClass("page_con_list");
            int pageSize = page == null || page.size() == 0 ? 0 : page.get(0).children().size();

            StringBuilder html = new StringBuilder(document.getElementsByClass("topicContent").html());
            // 循环获取余下的分页数据
            for (int i = 2; i <= pageSize; i++) {
                Document d = HttpUtil.getDocument(article.getUrl() + "?page=" + i);
                if (d == null) {
                    continue a;
                }
                html.append(d.getElementsByClass("topicContent").html());
            }

            saveGameData(article, html.toString());
            saveArticleContent(article, html.toString());
            updateArticle(article, document);

            threadSleep(2000);
        }
    }

    @Override
    public void updateArticle(ArticleInfoEntity articleInfoEntity, Document document) {
        String html = document.getElementsByClass("topicContent").html();

        articleInfoEntity.setHot(Integer.valueOf(document.getElementById("assistNum").text()));
        articleInfoEntity.setContentBreviary(HtmlUtil.getBreviary(html));
        articleInfoEntity.setStatus(ArticleStatusEnum.ALREADY.getStatus());
        articleInfoDao.save(articleInfoEntity);
    }

    @Override
    protected ArticleInfoEntity analysisArticle(String articleUrl, Integer releaseTime, WebInfoEntity webInfoEntity, Object articleObj) {
        Element article = null;
        JSONObject articleJson = null;
        if (articleObj instanceof Element) {
            article = (Element) articleObj;
        } else {
            articleJson = (JSONObject) articleObj;
        }
        boolean isJson = article == null;

        ArticleInfoEntity articleInfo = new ArticleInfoEntity();
        articleInfo.setAuthor(isJson ? articleJson.getString("author") : article.getElementsByClass("user_name").get(0).text());
        articleInfo.setCreateTime(DateUtil.getCurrentSecond());
        articleInfo.setReleaseTime(releaseTime);
        articleInfo.setSourceId(webInfoEntity.getId());
        articleInfo.setSource(webInfoEntity.getWebName());
        articleInfo.setTitle(isJson ? articleJson.getString("title") : article.getElementsByTag("h2").text());
        articleInfo.setUrl(articleUrl);
        articleInfo.setStatus(ArticleStatusEnum.NOT_YET.getStatus());
        articleInfo.setImageUrl(isJson ? articleJson.getString("cover") : article.getElementsByTag("img").attr("src"));
        return articleInfo;
    }
}
