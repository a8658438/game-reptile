package com.tc.reptile.dao;

import com.tc.reptile.entity.ArticleInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 22:03 2019/3/29
 */
public interface ArticleInfoDao extends JpaRepository<ArticleInfoEntity, Long> {
    Optional<ArticleInfoEntity> findByUrl(String url);
}
