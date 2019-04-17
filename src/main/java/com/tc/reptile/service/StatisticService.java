package com.tc.reptile.service;

import com.tc.reptile.constant.ArticleStatusEnum;
import com.tc.reptile.constant.YystvBordEnum;
import com.tc.reptile.dao.ArticleInfoDao;
import com.tc.reptile.dao.WebInfoDao;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.entity.WebInfoEntity;
import com.tc.reptile.model.WebArticleHotDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticService {
    private final WebInfoDao webInfoDao;
    private final ArticleInfoDao articleInfoDao;


    public StatisticService(WebInfoDao webInfoDao, ArticleInfoDao articleInfoDao) {
        this.webInfoDao = webInfoDao;
        this.articleInfoDao = articleInfoDao;
    }

    public List<WebArticleHotDTO> currentHotRank() {
        List<WebArticleHotDTO> list = new ArrayList<>();
        webInfoDao.findAll().forEach(web -> {
            WebArticleHotDTO hot = new WebArticleHotDTO();
            hot.setWebName(web.getWebName());
            List<ArticleInfoEntity> articleList = articleInfoDao.findAllByStatusAndSourceIdOrderByHotDesc(ArticleStatusEnum.ALREADY.getStatus(), web.getId());
            hot.setArticleList(articleList);
            list.add(hot);
        });
        return list;
    }


    /***
     * @Author: Chensr
     * @Description: 游戏变化排行
     * @Date: 2019/4/17 21:15
     * @param
     * @return: java.lang.Object
    */
    public Object changeRank() {
        return null;
    }
}
