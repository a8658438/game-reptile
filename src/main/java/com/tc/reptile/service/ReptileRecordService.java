package com.tc.reptile.service;

import com.tc.reptile.dao.ReptileRecordDao;
import com.tc.reptile.entity.ReptileRecordEntity;
import com.tc.reptile.util.DateUtil;
import org.springframework.stereotype.Service;

@Service
public class ReptileRecordService {
    private final ReptileRecordDao recordDao;

    public ReptileRecordService(ReptileRecordDao recordDao) {
        this.recordDao = recordDao;
    }


    public ReptileRecordEntity findReptileRecord() {
        return recordDao.findFirstByOrderByReptileTimeDesc().orElse(null);
    }

    /**
     * 生成爬取记录
     * @param size
     * @return
     */
    public Integer saveReptileRecord(Integer size) {
        // 生成爬取记录
        Integer currentSecond = DateUtil.getCurrentSecond();
        ReptileRecordEntity record = new ReptileRecordEntity();
        record.setId(currentSecond);
        record.setReptileCount(size);
        record.setFinishCount(0);
        record.setReptileTime(currentSecond);
        recordDao.save(record);
        return currentSecond;
    }

}
