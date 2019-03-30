package com.tc.reptile.dao;

import com.tc.reptile.entity.GameAppearRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 16:23 2019/3/30
 */
public interface GameAppearRecordDao extends JpaRepository<GameAppearRecordEntity, Long> {
}
