package com.tc.reptile.service;

import com.tc.reptile.dao.ReptileRecordDao;
import org.springframework.stereotype.Service;

@Service
public class ReptileRecordService {
    private final ReptileRecordDao recordDao;

    public ReptileRecordService(ReptileRecordDao recordDao) {
        this.recordDao = recordDao;
    }
}
