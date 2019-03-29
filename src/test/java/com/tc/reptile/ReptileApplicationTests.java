package com.tc.reptile;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReptileApplicationTests {
    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void contextLoads() {
        String url = "http://www.yystv.cn/home/get_home_docs_by_page";
        Map<String, String> map = new HashMap<>(1);
        map.put("page", "1");
        String responseEntity = restTemplate.getForObject(url, String.class, map);
        JSONObject jsonObject = JSONObject.parseObject(responseEntity);
        System.out.println(jsonObject);
    }

}
