package com.tc.reptile;

import com.tc.reptile.config.ReptileProperties;
import com.tc.reptile.service.ReptileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Properties;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReptileApplicationTests {
    @Autowired
    private ReptileService reptileService;
    @Autowired
    private ReptileProperties properties;
    @Test
    public void contextLoads() {
        reptileService.reptileArticleList();
    }

    @Test
    public void readProperties() {
        Integer readTime = properties.getReadTime();
        System.out.println(readTime);
    }

    @Test
    public void readContent() {
        reptileService.reptileArticleContent();
    }
}
