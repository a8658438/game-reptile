package com.tc.reptile.service;

import com.tc.reptile.dao.ArticleContentDao;
import com.tc.reptile.dao.ArticleInfoDao;
import com.tc.reptile.dao.GameAppearRecordDao;
import com.tc.reptile.dao.WebInfoDao;
import com.tc.reptile.entity.ArticleContentEntity;
import com.tc.reptile.entity.GameAppearRecordEntity;
import com.tc.reptile.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private final GameAppearRecordDao recordDao;
    private final WebInfoDao webInfoDao;

    public ArticleInfoService(ArticleInfoDao articleInfoDao, ArticleContentDao contentDao, GameAppearRecordDao recordDao, WebInfoDao webInfoDao) {
        this.articleInfoDao = articleInfoDao;
        this.contentDao = contentDao;
        this.recordDao = recordDao;
        this.webInfoDao = webInfoDao;
    }

    public PageDTO<ArticleInfoDTO> pageArticleList(ArticleParam param, PageParam page) {
        PageDTO<ArticleInfoDTO> articlePage = articleInfoDao.pageArticleList(param, page);
        // 查询关联的游戏
        articlePage.getContent().forEach(article -> {
            List<GameAppearRecordEntity> list = recordDao.findAllByArticleId(article.getId());
            StringBuilder games = new StringBuilder();
            list.forEach(record -> {
                games.append("、" + record.getGameName());
            });
            article.setIncludeGames(games.substring(1));
        });
        return articlePage;
    }

    public Optional<ArticleContentEntity> getContentByArticleId(Long articleId) {
        return contentDao.findByArticleId(articleId);
    }


}
