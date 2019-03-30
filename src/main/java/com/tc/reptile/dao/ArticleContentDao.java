package com.tc.reptile.dao;

import com.tc.reptile.entity.ArticleContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 16:26 2019/3/30
 */
public interface ArticleContentDao extends JpaRepository<ArticleContentEntity, Long> {
}
