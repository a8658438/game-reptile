package com.tc.reptile.controller;

import com.tc.reptile.entity.WebInfoEntity;
import com.tc.reptile.service.ReptileService;
import com.tc.reptile.service.WebInfoService;
import com.tc.reptile.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 20:51 2019/3/28
 */
@RestController
@RequestMapping("/reptile")
public class ReptileController {
    private Logger logger = LoggerFactory.getLogger(ReptileController.class);

    private final ReptileService reptileService;
    private final WebInfoService webInfoService;

    public ReptileController(ReptileService reptileService, WebInfoService webInfoService) {
        this.reptileService = reptileService;
        this.webInfoService = webInfoService;
    }

    @RequestMapping("/start")
    public String startReptile() {
        Optional<WebInfoEntity> optional = webInfoService.findById(1L);
        Map<String, Object> param = new HashMap<>();
        optional.ifPresent(webInfoEntity -> {
            for (int i = 0; i < 999; i++) {

                logger.info("开始爬取网站:{},当前爬取页数:{}", webInfoEntity.getWebName(), i);
                param.put("page", i);
                boolean b = reptileService.reptileArticleList(webInfoEntity, param);

                // 达到了停止爬取条件
                if (!b) {
                    webInfoEntity.setLastTime(DateUtil.getCurrentSecond());
                    webInfoService.save(webInfoEntity);

                    // 爬取文章内容
                    reptileService.reptileArticleContent();
                    break;
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
        return "200";
    }
}
