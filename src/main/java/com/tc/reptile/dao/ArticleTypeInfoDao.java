package com.tc.reptile.dao;

import com.tc.reptile.dao.jdbc.ArticleTypeInfoJdbcDao;
import com.tc.reptile.entity.ArticleTypeInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;

public interface ArticleTypeInfoDao extends JpaRepository<ArticleTypeInfoEntity, Long>, ArticleTypeInfoJdbcDao {
    List<ArticleTypeInfoEntity> findAllByArticleId(Long articleId);

}
