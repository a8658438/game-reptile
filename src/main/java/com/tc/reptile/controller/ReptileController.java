package com.tc.reptile.controller;

import com.tc.reptile.config.ReptileProperties;
import com.tc.reptile.entity.WebInfoEntity;
import com.tc.reptile.model.ResultVO;
import com.tc.reptile.service.CowlevelReptileService;
import com.tc.reptile.service.ReptileRecordService;
import com.tc.reptile.service.ReptileService;
import com.tc.reptile.service.WebInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 20:51 2019/3/28
 */
@RestController
@RequestMapping("/api/reptile")
public class ReptileController {
    private Logger logger = LoggerFactory.getLogger(ReptileController.class);

    private final ReptileService reptileService;
    private final WebInfoService webInfoService;
    private final ReptileProperties properties;
    private final CowlevelReptileService cowlevelReptileService;
    private final ReptileRecordService recordService;

    public ReptileController(ReptileService reptileService, WebInfoService webInfoService, ReptileProperties properties, CowlevelReptileService cowlevelReptileService, ReptileRecordService recordService) {
        this.reptileService = reptileService;
        this.webInfoService = webInfoService;
        this.properties = properties;
        this.cowlevelReptileService = cowlevelReptileService;
        this.recordService = recordService;
    }

    @RequestMapping("/start")
    public ResultVO startReptile(@RequestParam(value = "sourceIds[]", required = false) Integer[] sourceIds) {
        // 查询需要爬取的网站信息
        List<WebInfoEntity> webList = sourceIds == null ? webInfoService.findAll() : webInfoService.findAllByIdIn(sourceIds);

        // 排除已达到当天爬取上限的网站
        StringBuilder s = new StringBuilder();
        webList = webList.stream().filter(webInfoEntity -> {
            if (webInfoEntity.getReptileCount() < properties.getCountLimit()) {
                return true;
            }

            s.append("、" + webInfoEntity.getWebName());
            return false;
        }).collect(Collectors.toList());

        if (webList.isEmpty()) {
            return ResultVO.fail(String.format("爬取失败，以下网站已达到当天爬取次数限制：%s", s.substring(1)));
        }

        // 对爬取操作进行记录
        Integer id = recordService.saveReptileRecord(webList.size());
        webList.parallelStream().forEach(webInfoEntity -> reptileService.asyncReptileWeb(id, webInfoEntity));

        String message = "执行成功，请等待爬取工作结束";
        String msg = StringUtils.isEmpty(s.toString()) ? message :
                String.format(message + "。其中:【%s】已达到当日爬取次数限制，不再爬取。", s.substring(1));
        return ResultVO.ok(msg);

    }

    @RequestMapping("/getReptileRecord")
    public ResultVO getReptileRecord() {
        return ResultVO.of(recordService.findReptileRecord());
    }

    @RequestMapping("/test")
    public ResultVO test() {
        cowlevelReptileService.asyncReptileWeb(null, null);
        return ResultVO.ok();
    }

}
