package com.tc.reptile.dao.jdbc.impl;

import com.tc.reptile.dao.jdbc.ArticleTypeInfoJdbcDao;
import com.tc.reptile.util.DateUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class ArticleTypeInfoJdbcDaoImpl extends JdbcDaoSupport implements ArticleTypeInfoJdbcDao {
    Logger logger = LoggerFactory.getLogger(ArticleTypeInfoJdbcDaoImpl.class);

    @Autowired
    public ArticleTypeInfoJdbcDaoImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Override
    public List<Map<String, Object>> countType(Long webId) {
        String sql = "select t.type_name as type,count(1) as typeCount from article_type_info t where  t.create_time between ? and ? ";
        if (webId != null) {
            sql += " and t.source_id =" + webId;
        }
        sql += " group by t.type_name order by typeCount desc limit 100";

        DateTime dateTime = new DateTime();
        Integer endTime = DateUtil.getDayEndSecond(dateTime);
        Integer startTime = DateUtil.getDayStartSecond(dateTime.plusMonths(-3));

        logger.info(" data with SQL: {},param:{},{}", sql, startTime, endTime);
        return getJdbcTemplate().queryForList(sql, startTime, endTime);
    }
}
