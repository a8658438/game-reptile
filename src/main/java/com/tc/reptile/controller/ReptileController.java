package com.tc.reptile.controller;

import com.tc.reptile.service.ReptileService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 20:51 2019/3/28
 */
@RestController
@RequestMapping("/reptile")
public class ReptileController {
    private final ReptileService reptileService;

    public ReptileController(ReptileService reptileService) {
        this.reptileService = reptileService;
    }

    @RequestMapping("/start")
    public String startReptile(){
        reptileService.reptile();
        return "200";
    }
}
