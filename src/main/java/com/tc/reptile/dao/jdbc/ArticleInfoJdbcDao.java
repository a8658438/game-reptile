package com.tc.reptile.dao.jdbc;

import com.tc.reptile.model.ArticleInfoDTO;
import com.tc.reptile.model.ArticleParam;
import com.tc.reptile.model.PageDTO;
import com.tc.reptile.model.PageParam;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 22:03 2019/3/29
 */
public interface ArticleInfoJdbcDao {
    PageDTO<ArticleInfoDTO> pageArticleList(ArticleParam param, PageParam page);

    Integer countArticleByTimeRank(Integer startTime, Integer endTime);
}
