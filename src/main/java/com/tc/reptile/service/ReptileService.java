package com.tc.reptile.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tc.reptile.dao.ArticleInfoDao;
import com.tc.reptile.dao.WebInfoDao;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.entity.WebInfoEntity;
import com.tc.reptile.util.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 20:54 2019/3/29
 */
@Service
public class ReptileService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final WebInfoDao webInfoDao;
    private final ArticleInfoDao articleInfoDao;

    public ReptileService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao) {
        this.webInfoDao = webInfoDao;
        this.articleInfoDao = articleInfoDao;
    }

    /***
     * @Author: Chensr
     * @Description: 爬取网站数据
     * @Date: 2019/3/29 21:02
     * @param
     * @return: void
     */
    @Transactional
    public void reptile() {
        Optional<WebInfoEntity> optional = webInfoDao.findById(1L);
        Map<String, String> param = new HashMap<>();
        param.put("page", "1");

        optional.ifPresent(webInfoEntity -> {
            String response = restTemplate.getForObject(webInfoEntity.getUrl(), String.class, param);

            // 解析数据保存数据
            JSONObject json = JSONObject.parseObject(response);
            Object data = json.get("data");
            if (data == null) {
                return;
            }

            List<ArticleInfoEntity> list = new ArrayList<>();
            JSONArray array = (JSONArray) data;
            array.forEach(o -> {
                JSONObject article = (JSONObject) o;
                // 查询文章是否存在
                String articleUrl = webInfoEntity.getArticleUrl() + article.get("id");
                Optional<ArticleInfoEntity> infoEntity = articleInfoDao.findByUrl(articleUrl);
                if (infoEntity.isPresent()) {
                    return;
                }

                ArticleInfoEntity articleInfo = new ArticleInfoEntity();
                articleInfo.setAuthor((String) article.get("author"));
                articleInfo.setCreateTime(DateUtil.getCurrentSecond());
                articleInfo.setReleaseTime(DateUtil.getDateSecond((String) article.get("createtime"), DateUtil.FORMAT_TYPE_1));
                articleInfo.setSource(webInfoEntity.getWebName());
                articleInfo.setTitle((String) article.get("title"));
                articleInfo.setUrl(articleUrl);
                list.add(articleInfo);

            });

            articleInfoDao.saveAll(list);
        });
    }
}
