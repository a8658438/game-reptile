package com.tc.reptile.service;

import com.tc.reptile.config.ReptileProperties;
import com.tc.reptile.constant.ArticleStatusEnum;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: Chensr
 * @Description: gamelook
 * @Date: Create in 2:49 2019/4/28
 */
@Service
public class GameLookReptileService extends ReptileService {
    private Logger logger = LoggerFactory.getLogger(GameLookReptileService.class);

    public GameLookReptileService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao, ReptileProperties properties, GameAppearRecordDao recordDao, ArticleContentDao contentDao, ReptileRecordDao reptileRecordDao, ArticleTypeInfoDao articleTypeInfoDao) {
        super(webInfoDao, articleInfoDao, properties, recordDao, contentDao, reptileRecordDao, articleTypeInfoDao);
    }

    @Override
    public boolean reptileArticleList(WebInfoEntity webInfoEntity, Map<String, Object> param) {
        // 查询数据
        Document document = HttpUtil.getDocument(webInfoEntity.getUrl() + param.get("page"));
        if (document == null) {
            return false;
        }

        List<Element> articles = new ArrayList<>();
        document.getElementsByClass("article-list").forEach(element -> articles.addAll(element.getElementsByClass("item")));

        int count = 0; //计数器统计是否达到停止爬取条件。因为有的不同分类有相同文章
        for (Element article : articles) {
            String href = article.child(0).child(0).attr("href");
            System.out.println(article.child(0).child(0).attr("title"));
            Integer releaseTime = DateUtil.getDateSecond(article.getElementsByClass("date").get(0).text(), DateUtil.FORMAT_TYPE_1);

            // 判断是否达到停止爬取的条件
            if (stopReptile(webInfoEntity.getLastTime(), releaseTime, href)) {
                count++;
                continue;
            }

            ArticleInfoEntity articleInfoEntity = analysisArticle(href, releaseTime, webInfoEntity, article);
            articleInfoDao.save(articleInfoEntity);

            // 保存文章标签
            article.getElementsByClass("item-category").forEach(element ->
                saveArticleType(articleInfoEntity.getId(), webInfoEntity.getId(), element.text())
            );
        }

        return count == articles.size();
    }

    @Override
    public void reptileArticleContent(Long sourceId) {
        // 查询文章列表
        List<ArticleInfoEntity> articleList = articleInfoDao.findAllByStatusAndSourceId(ArticleStatusEnum.NOT_YET.getStatus(), sourceId);

        for (ArticleInfoEntity article : articleList) {
            logger.info("爬取文章内容，文章ID：{}", article.getId());
            Document document = HttpUtil.getDocument(article.getUrl());

            String html = document.getElementsByClass("entry-content").html();

            saveGameData(article, html);
            saveArticleContent(article, html);
            updateArticle(article, document);

            threadSleep(2000);
        }
    }

    @Override
    public void updateArticle(ArticleInfoEntity articleInfoEntity, Document document) {
        // 更新文章状态和点赞次数
//        articleInfoEntity.setHot(hot1.intValue());
//
//        // 获取作者信息
//        info.siblingElements().forEach(element -> {
//            if (element.text().contains("作者")) {
//                articleInfoEntity.setAuthor(element.text().replace("作者：", ""));
//            }
//        });
        // 获取缩略内容
        String html = document.getElementsByClass("entry-content").html();
        articleInfoEntity.setContentBreviary(HtmlUtil.getBreviary(html));
        articleInfoEntity.setStatus(ArticleStatusEnum.ALREADY.getStatus());
        articleInfoDao.save(articleInfoEntity);
    }

    @Override
    protected ArticleInfoEntity analysisArticle(String articleUrl, Integer releaseTime, WebInfoEntity webInfoEntity, Object articleObj) {
        Element article = (Element) articleObj;

        ArticleInfoEntity articleInfo = new ArticleInfoEntity();
        articleInfo.setCreateTime(DateUtil.getCurrentSecond());
        articleInfo.setReleaseTime(releaseTime);
        articleInfo.setSourceId(webInfoEntity.getId());
        articleInfo.setSource(webInfoEntity.getWebName());
        articleInfo.setUrl(articleUrl);
        articleInfo.setStatus(ArticleStatusEnum.NOT_YET.getStatus());
        articleInfo.setTitle(article.child(0).child(0).attr("title"));
        articleInfo.setImageUrl(article.getElementsByTag("img").get(0).attr("data-original"));
        return articleInfo;
    }
}
