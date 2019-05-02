package com.tc.reptile.service;

import com.alibaba.fastjson.JSONObject;
import com.tc.reptile.config.GamerSkyProperties;
import com.tc.reptile.config.ReptileProperties;
import com.tc.reptile.constant.ArticleStatusEnum;
import com.tc.reptile.dao.*;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.entity.WebInfoEntity;
import com.tc.reptile.util.DateUtil;
import com.tc.reptile.util.HttpUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 17:18 2019/4/29
 */
@Service
public class GamerSkyReptileService extends ReptileService{
    private Logger logger = LoggerFactory.getLogger(GamerSkyReptileService.class);
    private final GamerSkyProperties gamerSkyProperties;
    private final RestTemplate restTemplate = new RestTemplate();
    public GamerSkyReptileService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao, ReptileProperties properties, GameAppearRecordDao recordDao, ArticleContentDao contentDao, ReptileRecordDao reptileRecordDao, ArticleTypeInfoDao articleTypeInfoDao, GamerSkyProperties gamerSkyProperties) {
        super(webInfoDao, articleInfoDao, properties, recordDao, contentDao, reptileRecordDao, articleTypeInfoDao);
        this.gamerSkyProperties = gamerSkyProperties;
    }

    @Async
    public void asyncReptileWeb(Integer currentSecond, WebInfoEntity webInfoEntity){
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

        repticleComplete(currentSecond, webInfoEntity);
    }

    @Override
    public boolean reptileArticleList(WebInfoEntity webInfoEntity, Map<String, Object> param) {
        String jsonParam = String.format("{\"type\":\"updatenodelabel\",\"isCache\":true,\"cacheTime\":60,\"nodeId\":\"20401\",\"isNodeId\":\"true\",\"page\":%s}", param.get("page"));
        // 查询数据
        String result = restTemplate.getForObject(webInfoEntity.getUrl(), String.class, jsonParam);
        JSONObject resultJson = JSONObject.parseObject(result.substring(1, result.length() - 2));
        if (!resultJson.get("status").equals("ok")) {
            return false;
        }
        Elements articles = new Document("").html(resultJson.getString("body")).children();

        int count = 0; //计数器统计是否达到停止爬取条件。因为有的不同分类有相同文章
        for (Element article : articles) {
            String href = article.getElementsByTag("h3").get(0).child(0).attr("href");

            // 爬取文章，获取时间
            Document document = HttpUtil.getDocument(href);
            Integer releaseTime = DateUtil.getDateSecond(document.getElementsByClass("time").get(0).text(), "yyyy-MM-dd HH:mm:ss");
            // 判断是否达到停止爬取的条件
            if (stopReptile(webInfoEntity.getLastTime(), releaseTime, href)) {
                count++;
                continue;
            }

            ArticleInfoEntity articleInfoEntity = analysisArticle(href, releaseTime, webInfoEntity, article);

            // 查询文章热度
            String id = document.getElementsByClass("supportMe").get(0).attr("data-itemid");
            Integer hot = JSONObject.parseObject(restTemplate.getForObject(String.format(gamerSkyProperties.getHotUrl(), id), String.class)).getInteger("hits");
            articleInfoEntity.setHot(hot);
            articleInfoDao.save(articleInfoEntity);

            // 保存文章标签,部分文章没有标签
            Elements tags = article.getElementsByClass("tag");
            if (tags.size() != 0) {
                saveArticleType(articleInfoEntity.getId(), webInfoEntity.getId(), tags.get(0).child(0).text());
            }

            // 获取分页数据
            Elements page = document.getElementsByClass("page_css");
            int pageSize = page == null || page.size() == 0 ? 0 : page.get(0).children().size() - 2;

            StringBuilder html = new StringBuilder(document.getElementsByClass("Mid2L_con").get(0).html());
            // 循环获取余下的分页数据
            for (int i = 2; i <= pageSize; i++) {
                Document d = HttpUtil.getDocument(href.replace(".shtml", "_" + i + ".shtml"));
                html.append(d.getElementsByClass("Mid2L_con").get(0).html());
            }

           // 保存文章内容
            saveArticleContent(articleInfoEntity,html.toString());
            // 保存文章数据
            saveGameData(articleInfoEntity, html.toString());

            threadSleep(1500);
        }

        return count == articles.size();
    }

    @Override
    public void reptileArticleContent(Long sourceId) {

    }

    @Override
    public void updateArticle(ArticleInfoEntity articleInfoEntity, Document document) {

    }

    @Override
    protected ArticleInfoEntity analysisArticle(String articleUrl, Integer releaseTime, WebInfoEntity webInfoEntity, Object articleObj) {
        Element article = (Element) articleObj;

        ArticleInfoEntity articleInfo = new ArticleInfoEntity();
        articleInfo.setCreateTime(DateUtil.getCurrentSecond());
        articleInfo.setReleaseTime(releaseTime);
        articleInfo.setSourceId(webInfoEntity.getId());
        articleInfo.setSource(webInfoEntity.getWebName());
        articleInfo.setTitle(article.getElementsByTag("h3").get(0).child(0).text());
        articleInfo.setUrl(articleUrl);
        articleInfo.setStatus(ArticleStatusEnum.ALREADY.getStatus());
        articleInfo.setAuthor(article.getElementsByClass("lmu").text().trim());
        articleInfo.setImageUrl(article.getElementsByTag("img").get(0).attr("src"));
        articleInfo.setContentBreviary(article.getElementsByClass("txt").get(0).text());
        return articleInfo;
    }
}
