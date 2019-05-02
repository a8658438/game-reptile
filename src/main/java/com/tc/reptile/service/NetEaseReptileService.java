package com.tc.reptile.service;

import com.tc.reptile.config.NetEaseProperties;
import com.tc.reptile.config.ReptileProperties;
import com.tc.reptile.constant.ArticleStatusEnum;
import com.tc.reptile.dao.*;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.entity.WebInfoEntity;
import com.tc.reptile.util.DateUtil;
import com.tc.reptile.util.HtmlUtil;
import com.tc.reptile.util.HttpUtil;
import com.tc.reptile.util.RegexUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.tc.reptile.service.GameResReptileService.analysisDate;

/**
 * @Author: Chensr
 * @Description: 网易
 * @Date: Create in 13:57 2019/4/29
 */
@Service
public class NetEaseReptileService extends ReptileService {
    private Logger logger = LoggerFactory.getLogger(GameLookReptileService.class);
    private final NetEaseProperties netEaseProperties;

    public NetEaseReptileService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao, ReptileProperties properties, GameAppearRecordDao recordDao, ArticleContentDao contentDao, ReptileRecordDao reptileRecordDao, ArticleTypeInfoDao articleTypeInfoDao, NetEaseProperties netEaseProperties) {
        super(webInfoDao, articleInfoDao, properties, recordDao, contentDao, reptileRecordDao, articleTypeInfoDao);
        this.netEaseProperties = netEaseProperties;
    }

    @Override
    public boolean reptileArticleList(WebInfoEntity webInfoEntity, Map<String, Object> param) {
        // 查询数据
        Integer page = (Integer) param.get("page");
        String url = page.equals(1) ? webInfoEntity.getUrl() : String.format(netEaseProperties.getUrl(), page < 10 ? "0" + page : page);
        Document document = HttpUtil.getDocument(url);
        if (document == null) {
            return false;
        }
        Elements articles = document.getElementsByClass("item");

        int count = 0; //计数器统计是否达到停止爬取条件。因为有的不同分类有相同文章
        for (Element article : articles) {
            String href = article.child(0).attr("href");
            Integer releaseTime = DateUtil.getDateSecond("20" + RegexUtil.findStr(href, RegexUtil.REGEX_DATE).replace("/", ""), DateUtil.FORMAT_TYPE_2);

            // 判断是否达到停止爬取的条件
            if (stopReptile(webInfoEntity.getLastTime(), releaseTime, href)) {
                count++;
                continue;
            }

            ArticleInfoEntity articleInfoEntity = analysisArticle(href, releaseTime, webInfoEntity, article);
            articleInfoDao.save(articleInfoEntity);

            // 保存文章标签,部分文章没有标签
            Elements label = article.getElementsByClass("label");
            if (label.size() != 0) {
                saveArticleType(articleInfoEntity.getId(), webInfoEntity.getId(), label.get(0).text());
            }
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
            if (document == null) { // 出现超时的情况，留到下次爬取
                continue;
            }

            String html = document.getElementById("endText").html();

            saveGameData(article, html);
            saveArticleContent(article, html);
            updateArticle(article, document);

            threadSleep(2000);
        }
    }

    @Override
    public void updateArticle(ArticleInfoEntity articleInfoEntity, Document document) {
        String html = document.getElementById("endText").html();
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
        articleInfo.setAuthor(article.getElementsByClass("author").get(0).text());
        articleInfo.setTitle(article.getElementsByClass("t").get(0).text());
        articleInfo.setUrl(articleUrl);
        articleInfo.setHot(Integer.valueOf(article.getElementsByClass("comment").get(0).text()));
        articleInfo.setStatus(ArticleStatusEnum.NOT_YET.getStatus());
        articleInfo.setImageUrl(article.getElementsByClass("img").get(0).attr("src"));
        return articleInfo;
    }
}
