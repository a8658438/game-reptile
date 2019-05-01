package com.tc.reptile.job;

import com.tc.reptile.controller.ReptileController;
import com.tc.reptile.service.WebInfoService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 17:12 2019/4/17
 */
@Component
@Configuration
@EnableScheduling
public class ReptileTask {
    private final WebInfoService webInfoService;
    private final ReptileController reptileController;
    public ReptileTask(WebInfoService webInfoService, ReptileController reptileController) {
        this.webInfoService = webInfoService;
        this.reptileController = reptileController;
    }


    /***
     * @Author: Chensr
     * @Description: 每天早上8点定时爬取数据
     * @Date: 2019/4/17 17:27
     * @param
     * @return: void
    */
    @Scheduled(cron = "0 0 8 * * ?")
    public void reptile() {
        System.out.println("执行定时爬虫任务");
        reptileController.startReptile(new Long[]{});
    }

    /***
     * @Author: Chensr
     * @Description: 每天晚上12点定时清空爬取次数
     * @Date: 2019/4/17 17:27
     * @param
     * @return: void
    */
    @Scheduled(cron = "0/30 0 0 * * ?")
    public void resetReptileCount() {
        webInfoService.resetReptileCount();
    }
}
