package com.tc.reptile.controller;

import com.tc.reptile.entity.WebInfoEntity;
import com.tc.reptile.model.ResultVO;
import com.tc.reptile.service.ReptileService;
import com.tc.reptile.service.WebInfoService;
import com.tc.reptile.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public ReptileController(ReptileService reptileService, WebInfoService webInfoService) {
        this.reptileService = reptileService;
        this.webInfoService = webInfoService;
    }

    @RequestMapping("/start")
    public ResultVO startReptile(@RequestParam(value = "sourceIds[]" ,required = false) Integer[] sourceIds) {
        reptileService.startReptile(sourceIds);
        return ResultVO.ok();
    }
}
