package com.tc.reptile.dao.jdbc.impl;

import com.tc.reptile.dao.jdbc.ArticleInfoJdbcDao;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.model.ArticleParam;
import com.tc.reptile.model.PageDTO;
import com.tc.reptile.model.PageParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 22:03 2019/3/29
 */
public class ArticleInfoJdbcDaoImpl extends JdbcDaoSupport implements ArticleInfoJdbcDao {
    Logger logger = LoggerFactory.getLogger(ArticleInfoJdbcDaoImpl.class);

    @Autowired
    public ArticleInfoJdbcDaoImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public PageDTO<ArticleInfoEntity> pageArticleList(ArticleParam param, PageParam page) {
        StringBuilder sql = new StringBuilder(" from article_info t");
        if (!StringUtils.isEmpty(param.getContent())) {
            sql.append(" left join article_content c on t.id = c.article_id ");
        }

        sql.append("  where t.status = 1");
        List<Object> params = new ArrayList<>();
        if (param.getSourceId() != null) {
            sql.append(" and t.source_id = ?");
            params.add(param.getSourceId());
        }
        if (param.getStartDate() != null) {
            sql.append(" and t.release_time >= ? ");
            params.add(param.getStartDate());
        }
        if (param.getEndDate() != null) {
            sql.append(" and t.release_time <= ?");
            params.add(param.getEndDate());
        }
        if (!StringUtils.isEmpty(param.getTitle())) {
            sql.append(" and t.title like concat('%',?,'%')");
            params.add(param.getTitle());
        }
        if (!StringUtils.isEmpty(param.getContent())) {
            sql.append(" and c.content like concat('%',?,'%')");
            params.add(param.getContent());
        }

        String countSql = "select count(1) " + sql;
        logger.info("count with SQL: {},param:{}", countSql, params.toArray());
        Long total = getJdbcTemplate().queryForObject(countSql, params.toArray(), Long.class);

        sql.append(" order by t.release_time desc limit ?,?");
        params.add(page.getPage() - 1);
        params.add(page.getPageSize());

        String querySql = "select t.* " + sql;
        logger.info(" data with SQL: {},param:{}", querySql, params.toArray());
        List<ArticleInfoEntity> result = getJdbcTemplate().query(querySql, params.toArray(), new BeanPropertyRowMapper<>(ArticleInfoEntity.class));
        return PageDTO.of(total, result);
    }
}
