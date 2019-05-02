package com.tc.reptile.service;

import com.tc.reptile.config.ReptileProperties;
import com.tc.reptile.constant.ArticleStatusEnum;
import com.tc.reptile.dao.*;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.entity.WebInfoEntity;
import com.tc.reptile.util.DateUtil;
import com.tc.reptile.util.HttpUtil;
import com.tc.reptile.util.RegexUtil;
import org.joda.time.DateTime;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @Author: Chensr
 * @Description: 游资网
 * @Date: Create in 22:48 2019/4/27
 */
@Service
public class GameResReptileService extends ReptileService {
    private Logger logger = LoggerFactory.getLogger(GameResReptileService.class);

    public GameResReptileService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao, ReptileProperties properties, GameAppearRecordDao recordDao, ArticleContentDao contentDao, ReptileRecordDao reptileRecordDao, ArticleTypeInfoDao articleTypeInfoDao) {
        super(webInfoDao, articleInfoDao, properties, recordDao, contentDao, reptileRecordDao, articleTypeInfoDao);
    }

    @Override
    public boolean reptileArticleList(WebInfoEntity webInfoEntity, Map<String, Object> param) {
        // 查询数据
        Document document = HttpUtil.getDocument(String.format(webInfoEntity.getUrl(), param.get("page")));
        if (document == null) {
            return false;
        }
        Elements articles = document.getElementsByTag("article");

        int count = 0; //计数器统计是否达到停止爬取条件。因为有的不同分类有相同文章
        for (Element article : articles) {
            Element articleDiv = article.getElementsByClass("feed-item-right").get(0);
            String href = webInfoEntity.getArticleUrl() + articleDiv.child(0).attr("href");
            Integer releaseTime = analysisDate(articleDiv.child(2).text());

            // 判断是否达到停止爬取的条件
            if (stopReptile(webInfoEntity.getLastTime(), releaseTime, href)) {
                count++;
                continue;
            }

            ArticleInfoEntity articleInfoEntity = analysisArticle(href, releaseTime, webInfoEntity, article);
            articleInfoDao.save(articleInfoEntity);

            // 保存文章标签
            articleDiv.getElementsByClass("typespan").forEach(element -> {
                saveArticleType(articleInfoEntity.getId(), webInfoEntity.getId(), element.text());
            });
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
        if ((str.contains("天") || str.contains("小时"))) {
            return DateUtil.getDateSecond(str, null);
        }
        str = RegexUtil.replaceReg(RegexUtil.replaceReg(str, RegexUtil.REGEX_CHINESE), RegexUtil.REGEX_ENGLISH);

        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat(DateUtil.FORMAT_TYPE_1);
        try {
            format.setLenient(false);
            format.parse(str);
            return DateUtil.getDateSecond(str, DateUtil.FORMAT_TYPE_1);
        } catch (ParseException e) {
            return DateUtil.getDateSecond(new DateTime().getYear() + "-" + str, DateUtil.FORMAT_TYPE_1);
        }
    }

    @Override
    public void reptileArticleContent(Long sourceId) {
        // 查询文章列表
        List<ArticleInfoEntity> articleList = articleInfoDao.findAllByStatusAndSourceId(ArticleStatusEnum.NOT_YET.getStatus(), sourceId);

        for (ArticleInfoEntity article : articleList) {
            logger.info("爬取文章内容，文章ID：{}", article.getId());
            Document document = HttpUtil.getDocument(article.getUrl());
            if (document == null) {
                continue;
            }

            String html = document.getElementsByClass("t_f").html();

            saveGameData(article, html);
            saveArticleContent(article, html);
            updateArticle(article, document);

            threadSleep(2000);
        }
    }

    @Override
    public void updateArticle(ArticleInfoEntity articleInfoEntity, Document document) {
        // 更新文章状态和点赞次数
        Elements elements = document.getElementsByClass("layui-icon-fire");
        if (elements.size() != 0) { // 部分文章没有作者和热度
            Element info = elements.get(0).parent();
            Double hot1 = Double.parseDouble(info.text().replace("k", "")) * 1000;
            articleInfoEntity.setHot(hot1.intValue());

            // 获取作者信息
            info.siblingElements().forEach(element -> {
                if (element.text().contains("作者")) {
                    articleInfoEntity.setAuthor(element.text().replace("作者：", ""));
                }
            });
        }

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
        articleInfo.setTitle(article.getElementsByClass("feed-item-right").get(0).child(0).child(0).text());
        articleInfo.setUrl(articleUrl);
        articleInfo.setStatus(ArticleStatusEnum.NOT_YET.getStatus());
        articleInfo.setImageUrl(article.getElementsByClass("thumb").get(0).attr("data-original"));
        articleInfo.setContentBreviary(article.getElementsByTag("p").get(0).text());
        return articleInfo;
    }
}
