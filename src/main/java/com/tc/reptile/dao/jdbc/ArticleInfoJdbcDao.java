package com.tc.reptile.dao.jdbc;

import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.model.ArticleParam;
import com.tc.reptile.model.PageDTO;
import com.tc.reptile.model.PageParam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 22:03 2019/3/29
 */
public interface ArticleInfoJdbcDao {
    PageDTO<ArticleInfoEntity> pageArticleList(ArticleParam param, PageParam page);
}
