package com.tc.reptile.dao;

import com.tc.reptile.dao.jdbc.GameAppearRecordJdbcDao;
import com.tc.reptile.entity.GameAppearRecordEntity;
import com.tc.reptile.model.GameChangeDTO;
import com.tc.reptile.model.GameCountDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 16:23 2019/3/30
 */
public interface GameAppearRecordDao extends JpaRepository<GameAppearRecordEntity, Long> , GameAppearRecordJdbcDao{
    List<GameAppearRecordEntity> findAllByArticleId(Long articleId);
}
