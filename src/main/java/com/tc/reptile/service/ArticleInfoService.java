package com.tc.reptile.service;

import com.tc.reptile.dao.ArticleContentDao;
import com.tc.reptile.dao.ArticleInfoDao;
import com.tc.reptile.entity.ArticleContentEntity;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.model.ArticleParam;
import com.tc.reptile.model.PageDTO;
import com.tc.reptile.model.PageParam;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 19:03 2019/4/13
 */
@Service
public class ArticleInfoService {
    private final ArticleInfoDao articleInfoDao;
    private final ArticleContentDao contentDao;

    public ArticleInfoService(ArticleInfoDao articleInfoDao, ArticleContentDao contentDao) {
        this.articleInfoDao = articleInfoDao;
        this.contentDao = contentDao;
    }

    public PageDTO<ArticleInfoEntity> pageArticleList(ArticleParam param, PageParam page) {
        return articleInfoDao.pageArticleList(param, page);
    }

    public Optional<ArticleContentEntity> getContentByArticleId(Long articleId) {
        return contentDao.findByArticleId(articleId);
    }
}
