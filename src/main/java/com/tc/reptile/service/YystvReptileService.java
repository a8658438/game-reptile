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
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Service
public class YystvReptileService extends ReptileService {
    private Logger logger = LoggerFactory.getLogger(ReptileService.class);

    public YystvReptileService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao, ReptileProperties properties, GameAppearRecordDao recordDao, ArticleContentDao contentDao, ReptileRecordDao reptileRecordDao, ArticleTypeInfoDao articleTypeInfoDao) {
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
        param.put("page", (int) param.get("page") - 1);
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

            list.add(analysisArticle(articleUrl, releaseTime, webInfoEntity, article));
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

    public void reptileArticleContent(Long sourceId) {
        // 查询文章列表
        List<ArticleInfoEntity> articleList = articleInfoDao.findAllByStatusAndSourceId(ArticleStatusEnum.NOT_YET.getStatus(), sourceId);

        for (ArticleInfoEntity article : articleList) {
            logger.info("爬取文章内容，文章ID：{}", article.getId());
            Document document = HttpUtil.getYysArticleDocument(article.getUrl(), "http://www.yystv.cn/");
            if (document == null) { // 出现超时的情况，留到下次爬取
                continue;
            }

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
