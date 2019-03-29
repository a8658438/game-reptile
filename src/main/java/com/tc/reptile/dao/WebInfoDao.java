package com.tc.reptile.dao;

import com.tc.reptile.entity.WebInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 20:37 2019/3/29
 */
public interface WebInfoDao extends JpaRepository<WebInfoEntity, Long> {
}
