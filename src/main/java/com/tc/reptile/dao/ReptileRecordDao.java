package com.tc.reptile.dao;

import com.tc.reptile.entity.ReptileRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReptileRecordDao extends JpaRepository<ReptileRecordEntity, Long> {
    @Query(value = "update reptile_record t set t.finish_count = t.finish_count + 1, t.update_time = ? where t.id = ?", nativeQuery = true)
    @Modifying
    void updateRecord(Integer updateTime,Integer currentSecond);
}
