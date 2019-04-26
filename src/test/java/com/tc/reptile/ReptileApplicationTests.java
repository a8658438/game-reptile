package com.tc.reptile;

import com.tc.reptile.config.ReptileProperties;
import com.tc.reptile.entity.WebInfoEntity;
import com.tc.reptile.service.ReptileService;
import com.tc.reptile.service.WebInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReptileApplicationTests {
    @Autowired
    private WebInfoService webInfoService;
    @Autowired
    private ReptileProperties properties;

    @Test
    public void contextLoads() {
        Optional<WebInfoEntity> optional = webInfoService.findById(1L);
        Map<String, Object> param = new HashMap<>();
        optional.ifPresent(webInfoEntity -> {
            param.put ("page", 3);
//            reptileService.reptileArticleList(webInfoEntity, param);
        });
    }

    @Test
    public void readProperties() {
        Integer readTime = properties.getReadTime();
        System.out.println(readTime);
    }

    @Test
    public void readContent() {
//        reptileService.reptileArticleContent();
    }
}
