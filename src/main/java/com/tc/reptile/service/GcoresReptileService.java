package com.tc.reptile.service;

import com.tc.reptile.config.ReptileProperties;
import com.tc.reptile.constant.ArticleStatusEnum;
import com.tc.reptile.dao.*;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.entity.WebInfoEntity;
import com.tc.reptile.util.DateUtil;
import com.tc.reptile.util.HtmlUtil;
import com.tc.reptile.util.HttpUtil;
import com.tc.reptile.util.RegexUtil;
import org.aspectj.weaver.ast.Var;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: Chensr
 * @Description: 机核
 * @Date: Create in 15:45 2019/4/30
 */
@Service
public class GcoresReptileService extends ReptileService {
    private Logger logger = LoggerFactory.getLogger(GcoresReptileService.class);

    public GcoresReptileService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao, ReptileProperties properties, GameAppearRecordDao recordDao, ArticleContentDao contentDao, ReptileRecordDao reptileRecordDao, ArticleTypeInfoDao articleTypeInfoDao) {
        super(webInfoDao, articleInfoDao, properties, recordDao, contentDao, reptileRecordDao, articleTypeInfoDao);
    }

    @Override
    public void asyncReptileWeb(Integer currentSecond, WebInfoEntity webInfoEntity) {
        String articleUrl = webInfoEntity.getUrl();
        for (int x = 1; x <= 2; x++) {
            for (int i = 1; i < 999; i++) {

                webInfoEntity.setUrl(String.format(articleUrl, x, i));
                logger.info("开始爬取网站:{},当前爬取页数:{}, 分类URL：{}", webInfoEntity.getWebName(), i, webInfoEntity.getUrl());

                boolean b = reptileArticleList(webInfoEntity, null);

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
        Document document = HttpUtil.getDocument(webInfoEntity.getUrl());
        if (document == null) {
            return false;
        }

        Elements articles = document.getElementsByClass("row").get(0).children();
        int count = 0; //计数器统计是否达到停止爬取条件。因为有的不同分类有相同文章
        for (Element article : articles) {
            String href = article.getElementsByTag("h4").get(0).child(0).attr("href");
            Element showcaseTime = article.getElementsByClass("showcase_time").get(0);
            Integer releaseTime = analysisDate(showcaseTime.text());

            // 判断是否达到停止爬取的条件
            if (stopReptile(webInfoEntity.getLastTime(), releaseTime, href)) {
                count++;
                continue;
            }

            ArticleInfoEntity articleInfoEntity = analysisArticle(href, releaseTime, webInfoEntity, article);
            articleInfoDao.save(articleInfoEntity);

            // 保存文章标签
            saveArticleType(articleInfoEntity.getId(), webInfoEntity.getId(), showcaseTime.child(0).child(0).text().trim());
        }

        return count == articles.size();
    }

    /***
     * @Author: Chensr
     * @Description: 解析网站时间
     * @Date: 2019/4/28 0:53
     * @param str
     * @return: java.lang.String
     */
    public static Integer analysisDate(String str) {
        str = RegexUtil.replaceReg(RegexUtil.replaceReg(str, RegexUtil.REGEX_CHINESE), RegexUtil.REGEX_ENGLISH).trim();

        return DateUtil.getDateSecond(str, DateUtil.FORMAT_TYPE_1);
    }

    @Override
    public void reptileArticleContent(Long sourceId) {
        // 查询文章列表
        List<ArticleInfoEntity> articleList = articleInfoDao.findAllByStatusAndSourceId(ArticleStatusEnum.NOT_YET.getStatus(), sourceId);

        for (ArticleInfoEntity article : articleList) {
            logger.info("爬取文章内容，文章ID：{}", article.getId());
            Document document = HttpUtil.getDocument(article.getUrl());

            String html = document.getElementsByClass("story").html();

            saveGameData(article, html);
            saveArticleContent(article, html);
            updateArticle(article, document);

            threadSleep(2000);
        }
    }

    @Override
    public void updateArticle(ArticleInfoEntity articleInfoEntity, Document document) {
        Elements elements = document.getElementsByClass("story_user_name");
        // 机核官方文章没有作者名称
        if (elements.size() != 0) {
            articleInfoEntity.setAuthor(elements.get(0).text().trim());
        }
        articleInfoEntity.setContentBreviary(HtmlUtil.getBreviary(document.getElementsByClass("story").html()));
        articleInfoEntity.setStatus(ArticleStatusEnum.ALREADY.getStatus());
        articleInfoDao.save(articleInfoEntity);
    }

    @Override
    protected ArticleInfoEntity analysisArticle(String articleUrl, Integer releaseTime, WebInfoEntity webInfoEntity, Object articleObj) {
        Element article = (Element) articleObj;
        Element img = article.getElementsByTag("img").get(0);
        ArticleInfoEntity articleInfo = new ArticleInfoEntity();
        articleInfo.setCreateTime(DateUtil.getCurrentSecond());
        articleInfo.setReleaseTime(releaseTime);
        articleInfo.setSourceId(webInfoEntity.getId());
        articleInfo.setSource(webInfoEntity.getWebName());
        articleInfo.setTitle(img.attr("alt"));
        articleInfo.setUrl(articleUrl);
        articleInfo.setStatus(ArticleStatusEnum.NOT_YET.getStatus());
        articleInfo.setImageUrl(img.attr("src"));
        articleInfo.setHot(Integer.valueOf(article.getElementsByClass("showcase_meta_nums").get(0).child(0).text().trim()));
        return articleInfo;
    }
}
