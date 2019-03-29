package com.tc.reptile.controller;

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


    @RequestMapping("/start")
    public String startReptile(){

        return "200";
    }
}
