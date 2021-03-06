package com.tc.reptile.dao;

import com.tc.reptile.dao.jdbc.ArticleInfoJdbcDao;
import com.tc.reptile.entity.ArticleInfoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 22:03 2019/3/29
 */
public interface ArticleInfoDao extends JpaRepository<ArticleInfoEntity, Long>, ArticleInfoJdbcDao {
    Optional<ArticleInfoEntity> findByUrl(String url);

    List<ArticleInfoEntity> findAllByStatusAndSourceId(Integer status, Long sourceId);
    List<ArticleInfoEntity> findAllByStatusAndSourceIdOrderByHotDesc(Integer status, Long sourceId, Pageable pageable);
}
