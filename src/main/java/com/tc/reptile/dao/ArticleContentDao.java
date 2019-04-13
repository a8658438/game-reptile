package com.tc.reptile.dao;

import com.tc.reptile.dao.jdbc.ArticleInfoJdbcDao;
import com.tc.reptile.entity.ArticleContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 16:26 2019/3/30
 */
public interface ArticleContentDao extends JpaRepository<ArticleContentEntity, Long>, ArticleInfoJdbcDao {
    Optional<ArticleContentEntity> findByArticleId(Long articleId);
}
