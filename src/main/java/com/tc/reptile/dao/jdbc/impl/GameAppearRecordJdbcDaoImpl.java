package com.tc.reptile.dao.jdbc.impl;

import com.tc.reptile.dao.jdbc.GameAppearRecordJdbcDao;
import com.tc.reptile.model.GameChangeDTO;
import com.tc.reptile.model.GameCountDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import java.util.List;

public class GameAppearRecordJdbcDaoImpl extends JdbcDaoSupport implements GameAppearRecordJdbcDao{
    Logger logger = LoggerFactory.getLogger(ArticleInfoJdbcDaoImpl.class);

    @Autowired
    public GameAppearRecordJdbcDaoImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Override
    public List<GameChangeDTO> gameCount(long startDate, long endDate) {
        String sql = "select t.game_name,count(1) as total from game_appear_record t where t.release_time between ? and ? group by t.game_name";
        logger.info(" data with SQL: {},param:{},{}", sql, startDate, endDate);
        return getJdbcTemplate().query(sql, new Object[]{startDate, endDate}, new BeanPropertyRowMapper<>(GameChangeDTO.class));
    }

    @Override
    public List<GameCountDTO> gameCount() {
        String sql = "select t.game_name, count(1) as total from game_appear_record t GROUP BY t.game_name";
        logger.info(" data with SQL: {},param:{},{}", sql);
        return  getJdbcTemplate().query(sql, new Object[]{}, new BeanPropertyRowMapper<>(GameCountDTO.class));
    }
}
