package com.tc.reptile.controller;

import com.tc.reptile.model.ResultVO;
import com.tc.reptile.service.StatisticService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistic")
public class StatisticController {
    private final StatisticService service;

    public StatisticController(StatisticService statisticService) {
        this.service = statisticService;
    }

    @PostMapping("/currentHotRank")
    public ResultVO currentHotRank() {
        return ResultVO.of(service.currentHotRank());
    }

    @PostMapping("changeRank")
    public ResultVO changeRank() {
        return ResultVO.of(service.changeRank());
    }

    @PostMapping("gameCount")
    public ResultVO gameCount() {
        return ResultVO.of(service.gameCount());
    }

    @PostMapping("/articleChangRank")
    public ResultVO articleChangRank() {
        return ResultVO.of(service.articleChangRank());
    }

    @PostMapping("/articleTypeCount")
    public ResultVO articleTypeCount() {
        return ResultVO.of(service.articleTypeCount());
    }
}
