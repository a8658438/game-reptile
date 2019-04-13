package com.tc.reptile.service;

import com.tc.reptile.dao.WebInfoDao;
import com.tc.reptile.entity.WebInfoEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 14:59 2019/4/13
 */
@Service
public class WebInfoService {
    private final WebInfoDao webInfoDao;

    public WebInfoService(WebInfoDao webInfoDao) {
        this.webInfoDao = webInfoDao;
    }

    /***
     * @Author: Chensr
     * @Description: 查找网站
     * @Date: 2019/4/13 15:01
     * @param id
     * @return: java.util.Optional<com.tc.reptile.entity.WebInfoEntity>
    */
    public Optional<WebInfoEntity> findById(Long id) {
        return webInfoDao.findById(id);
    }

    public void save(WebInfoEntity webInfoEntity) {
        webInfoDao.save(webInfoEntity);
    }

    /**
     * 查询所有网站列表
     * @return
     */
    public List<WebInfoEntity> findAll() {
        return webInfoDao.findAll();
    }
}
