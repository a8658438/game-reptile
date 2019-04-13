package com.tc.reptile.controller;

import com.tc.reptile.model.ResultVO;
import com.tc.reptile.service.WebInfoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 19:19 2019/4/13
 */
@RestController
@RequestMapping("/web")
public class WebInfoController {
    private final WebInfoService webInfoService;

    public WebInfoController(WebInfoService webInfoService) {
        this.webInfoService = webInfoService;
    }

    @PostMapping("/list")
    public ResultVO getWebList() {
        return ResultVO.of(webInfoService.findAll());
    }
}
