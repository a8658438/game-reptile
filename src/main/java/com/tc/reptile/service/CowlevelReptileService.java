package com.tc.reptile.service;

import com.alibaba.fastjson.JSONObject;
import com.tc.reptile.config.CowlevelProperties;
import com.tc.reptile.config.ReptileProperties;
import com.tc.reptile.constant.ArticleStatusEnum;
import com.tc.reptile.constant.CowlevelConstant;
import com.tc.reptile.dao.*;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.entity.WebInfoEntity;
import com.tc.reptile.util.DateUtil;
import com.tc.reptile.util.HttpUtil;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 17:35 2019/4/24
 */
@Service
public class CowlevelReptileService extends ReptileService {
    private Logger logger = LoggerFactory.getLogger(CowlevelReptileService.class);
    private String token;

    private final CowlevelProperties cowlevelProperties;

    public CowlevelReptileService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao, ReptileProperties properties, GameAppearRecordDao recordDao, ArticleContentDao contentDao, ReptileRecordDao reptileRecordDao, CowlevelProperties cowlevelProperties) {
        super(webInfoDao, articleInfoDao, properties, recordDao, contentDao, reptileRecordDao);
        this.cowlevelProperties = cowlevelProperties;
    }

    /***
     * @Author: Chensr
     * @Description: 登录方法，返回token
     * @Date: 2019/4/24 18:04
     * @param
     * @return: java.lang.String
     */
    public String login() {
        Map<String, String> requestEntity = new HashMap<>();
        requestEntity.put(CowlevelConstant.ACCOUNT, cowlevelProperties.getAccount());
        requestEntity.put(CowlevelConstant.PASSWORD, cowlevelProperties.getPassword());
        Optional<JSONObject> jsonObject = HttpUtil.postDataForJson(cowlevelProperties.getLoginUrl(), requestEntity);
        return jsonObject.isPresent() ? (String) jsonObject.get().get(CowlevelConstant.TOKEN) : null;
    }

    /**
     * 构建请求header带上cookie
     *
     * @return
     */
    private HttpHeaders buildHttpHeader() {
        List<String> list = new ArrayList<>();
        list.add(CowlevelConstant.TOKEN + "=" + token);
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.COOKIE, list);
        return headers;
    }

    @Override
    public void asyncReptileWeb(Integer currentSecond, WebInfoEntity webInfoEntity) {
        // 校验是否登录成功
        token = login();
        if (StringUtils.isEmpty(token)) {
            return;
        }

        // 获取网站分类
        Map<String, Object> param = new HashMap<>();
        param.put("is_rich_content", 1);
        param.put("per_page", 50); // 每页数量
        param.put("sort_type", "desc");

        List<JSONObject> typeList = getArticleTypeList();
        for (JSONObject type : typeList) {
            for (int i = 1; i < 999; i++) {
                Object typeName = type.get("name");
                logger.info("开始爬取网站:{},当前爬取分类:{},当前爬取页数:{}", webInfoEntity.getWebName(), typeName, i);

                param.put("page", i);
                param.put("tag_id", type.get("id"));
                param.put("type_name", typeName);

                boolean b = reptileArticleList(webInfoEntity, param);
                // 达到了停止爬取条件
                if (b) {
                    break;
                }
                threadSleep(2000);
            }
        }

        // 更新网站信息
        repticleComplete(currentSecond, webInfoEntity);
    }

    @Override
    protected ArticleInfoEntity analysisArticle(String articleUrl, Integer releaseTime, WebInfoEntity webInfoEntity, JSONObject article, String type) {
        ArticleInfoEntity articleInfo = new ArticleInfoEntity();
        articleInfo.setAuthor(((JSONObject) article.get(CowlevelConstant.ARTICLE_AUTHOR)).getString(CowlevelConstant.ARTICLE_AUTHOR_NAME));
        articleInfo.setCreateTime(DateUtil.getCurrentSecond());
        articleInfo.setReleaseTime(releaseTime);
        articleInfo.setSourceId(webInfoEntity.getId());
        articleInfo.setSource(webInfoEntity.getWebName());
        articleInfo.setTitle(article.getString(CowlevelConstant.ARTICLE_TITLE));
        articleInfo.setUrl(articleUrl);
        articleInfo.setStatus(ArticleStatusEnum.ALREADY.getStatus());
        articleInfo.setImageUrl(article.getString(CowlevelConstant.IMAGE_URL));
        articleInfo.setContentBreviary(((JSONObject) article.get(CowlevelConstant.BRIEF_CONTENT)).getString("desc"));
        articleInfo.setHot(article.getInteger(CowlevelConstant.HOT));
        articleInfo.setType(type);
        return articleInfo;
    }

    /***
     * @Author: Chensr
     * @Description: 获取分类列表
     * @Date: 2019/4/25 20:35
     * @param
     * @return: java.util.List<com.alibaba.fastjson.JSONObject>
     */
    private List<JSONObject> getArticleTypeList() {
        Map<String, Object> param = new HashMap<>();
        List<JSONObject> list = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            param.put("page", i);
            Optional<Object> data = HttpUtil.getDataForJson(cowlevelProperties.getTypeUrl(), buildHttpHeader(), param);

            if (!data.isPresent()) {
                break;
            }
            JSONObject json = (JSONObject) data.get();
            List typeList = (List) json.get("list");
            list.addAll(typeList);

            if ((int) json.get("has_more") == 0) {
                break;
            }

        }
        return list;
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
        // 查询数据
        Optional<Object> data = HttpUtil.getDataForJson(webInfoEntity.getUrl(), buildHttpHeader(), param);
        if (!data.isPresent()) {
            return false;
        }
        List array = (List) ((JSONObject) data.get()).get("list");

        int count = 0; //计数器统计是否达到停止爬取条件。因为有的不同分类有相同文章
        for (Object o : array) {
            JSONObject article = (JSONObject) o;
            // 查询文章是否存在或者 是否是过旧的数据
            String articleUrl = webInfoEntity.getArticleUrl() + article.get(CowlevelConstant.ARTICLE_ID);
            Integer releaseTime = article.getInteger(CowlevelConstant.ARTICLE_CREATETIME);

            // 判断是否达到停止爬取的条件
            if (stopReptile(webInfoEntity.getLastTime(), releaseTime, articleUrl)) {
                count++;
                continue;
            }

            // 解析文章信息并保存
            ArticleInfoEntity articleInfo = analysisArticle(articleUrl, releaseTime, webInfoEntity, article, (String) param.get("type_name"));
            articleInfoDao.save(articleInfo);
            // 保存文章内容
            saveArticleContent(articleInfo, article.getString(CowlevelConstant.CONTENT));
        }
        return count == array.size();
    }

    @Override
    public void reptileArticleContent(Long sourceId) {

    }


    @Override
    public void updateArticle(ArticleInfoEntity articleInfoEntity, Document document) {

    }
}
