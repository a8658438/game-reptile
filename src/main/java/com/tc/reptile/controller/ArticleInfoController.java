package com.tc.reptile.controller;

import com.tc.reptile.entity.ArticleContentEntity;
import com.tc.reptile.entity.ArticleInfoEntity;
import com.tc.reptile.model.ArticleParam;
import com.tc.reptile.model.PageDTO;
import com.tc.reptile.model.PageParam;
import com.tc.reptile.model.ResultVO;
import com.tc.reptile.service.ArticleInfoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 19:06 2019/4/13
 */
@RestController
@RequestMapping("/api/article")
public class ArticleInfoController {
    private final ArticleInfoService service;

    public ArticleInfoController(ArticleInfoService service) {
        this.service = service;
    }

    @PostMapping("page")
    public ResultVO pageArticleList(ArticleParam param, PageParam page) {
        return ResultVO.of(service.pageArticleList(param, page));
    }

    @PostMapping("/content")
    public ResultVO getContent(Long articleId) {
        Optional<ArticleContentEntity> optional = service.getContentByArticleId(articleId);
        return ResultVO.of(optional.isPresent() ? optional.get() : null);
    }

}
