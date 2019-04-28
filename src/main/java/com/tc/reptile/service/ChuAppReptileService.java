package com.tc.reptile.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tc.reptile.config.ReptileProperties;
import com.tc.reptile.constant.ArticleStatusEnum;
import com.tc.reptile.constant.YystvConstant;
import com.tc.reptile.dao.*;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.entity.WebInfoEntity;
import com.tc.reptile.util.DateUtil;
import com.tc.reptile.util.HtmlUtil;
import com.tc.reptile.util.HttpUtil;
import com.tc.reptile.util.RegexUtil;
import org.joda.time.DateTime;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: Chensr
 * @Description: 触乐
 * @Date: Create in 9:17 2019/4/28
 */
@Service
public class ChuAppReptileService extends ReptileService{
    private Logger logger = LoggerFactory.getLogger(ChuAppReptileService.class);
    public ChuAppReptileService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao, ReptileProperties properties, GameAppearRecordDao recordDao, ArticleContentDao contentDao, ReptileRecordDao reptileRecordDao, ArticleTypeInfoDao articleTypeInfoDao) {
        super(webInfoDao, articleInfoDao, properties, recordDao, contentDao, reptileRecordDao, articleTypeInfoDao);
    }

    /***
     * @Author: Chensr
     * @Description: 爬取网站数据
     * @Date: 2019/3/29 21:02
     * @param
     * @return: void
     */

    public boolean reptileArticleList(WebInfoEntity webInfoEntity, Map<String, Object> param) {
        List<ArticleInfoEntity> list = new ArrayList<>();
        // 查询数据
        Document document = HttpUtil.getDocument(String.format(webInfoEntity.getUrl(), param.get("page")));
        if (document == null) {
            return false;
        }

        Elements array = document.getElementsByClass("category-list").get(0).children();
        int count = 0; //计数器统计是否达到停止爬取条件。因为有的不同分类有相同文章
        for (Element article : array) {

            // 查询文章是否存在或者 是否是过旧的数据
            String articleUrl = webInfoEntity.getArticleUrl() + article.attr("href");
            String date = article.getElementsByTag("em").get(0).parent().text();
            Integer releaseTime = DateUtil.getDateSecond(analysisDate(date), DateUtil.FORMAT_TYPE_2);

            // 判断是否达到停止爬取的条件
            if (stopReptile(webInfoEntity.getLastTime(), releaseTime, articleUrl)) {
                count++;
                continue;
            }

            list.add(analysisArticle(articleUrl, releaseTime, webInfoEntity, article));
        }

        articleInfoDao.saveAll(list);
        return count == array.size();
    }

    /***
     * @Author: Chensr
     * @Description: 解析网站时间
     * @Date: 2019/4/28 0:53
     * @param str
     * @return: java.lang.String
     */
    public static String analysisDate(String str) {
        str = RegexUtil.replaceReg(RegexUtil.replaceReg(str, RegexUtil.REGEX_CHINESE), RegexUtil.REGEX_ENGLISH);

        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat(DateUtil.FORMAT_TYPE_2);
        try {
            format.setLenient(false);
            format.parse(str);
            return str;
        } catch (ParseException e) {
            return new DateTime().getYear() + str;
        }
    }

    /***
     * @Author: Chensr
     * @Description: 爬取文章内容数据
     * @Date: 2019/3/30 14:32
     * @param
     * @return: void
     */

    public void reptileArticleContent(Long sourceId) {
        // 查询文章列表
        List<ArticleInfoEntity> articleList = articleInfoDao.findAllByStatusAndSourceId(ArticleStatusEnum.NOT_YET.getStatus(), sourceId);

        for (ArticleInfoEntity article : articleList) {
            logger.info("爬取文章内容，文章ID：{}", article.getId());
            Document document = HttpUtil.getYysArticleDocument(article.getUrl(), "http://www.yystv.cn/");

            String html = document.getElementsByClass(YystvConstant.ARTICLE_CONTENT).get(0).child(0).html();
            String type = document.getElementsByClass(YystvConstant.ARTICLE_TYPE).get(0).text();

            saveArticleType(article.getId(),sourceId, type);
            saveGameData(article, html);
            saveArticleContent(article, html);
            updateArticle(article, document);

            threadSleep(2000);
        }

    }

    @Override
    public void updateArticle(ArticleInfoEntity articleInfoEntity, Document document) {
        String html = document.getElementsByClass(YystvConstant.ARTICLE_CONTENT).get(0).child(0).html();
        // 更新文章状态和点赞次数
        Elements tags = document.getElementsByClass(YystvConstant.HOT_CLASS);
        if (!tags.isEmpty()) {
            String count = tags.get(0).html();
            articleInfoEntity.setHot(StringUtils.isEmpty(count) ? 0 : Integer.parseInt(count));
        }

        articleInfoEntity.setContentBreviary(HtmlUtil.getBreviary(html));
        articleInfoEntity.setStatus(ArticleStatusEnum.ALREADY.getStatus());
        articleInfoDao.save(articleInfoEntity);
    }

    @Override
    protected ArticleInfoEntity analysisArticle(String articleUrl, Integer releaseTime, WebInfoEntity webInfoEntity, Object articleObj) {
        JSONObject article = (JSONObject) articleObj;
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
