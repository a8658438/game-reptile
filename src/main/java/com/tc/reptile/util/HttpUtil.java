package com.tc.reptile.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 14:42 2019/3/30
 */
public class HttpUtil {
    private static final RestTemplate restTemplate = new RestTemplate();

    /***
     * @Author: Chensr
     * @Description: 调用链接获取数据，返回data数组
     * @Date: 2019/3/30 14:45
     * @param url
     * @param param
     * @return: java.util.Optional<com.alibaba.fastjson.JSONArray>
    */
    public static Optional<JSONArray> getDataForJson(String url, Map<String,Object> param) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        param.forEach((k,v) -> {
            builder.queryParam(k, v);
        });
        String response = restTemplate.getForObject(builder.build().encode().toUri(), String.class);

        // 解析数据保存数据
        JSONObject json = JSONObject.parseObject(response);
        Object data = json.get("data");
        return data == null ? Optional.empty() : Optional.of((JSONArray) data);
    }

    /***
     * @Author: Chensr
     * @Description: 获取html页面的document
     * @Date: 2019/3/30 16:34
     * @param url
     * @param baseUrl
     * @return: org.jsoup.nodes.Document
    */
    public static Document getDocument(String url, String baseUrl) throws IOException {
        Document document = Jsoup.connect(url).get();
        Elements imgs = document.getElementsByTag("img");
        imgs.forEach(element -> {
            String href = element.attr("data-original");
            element.attr("src", href);
        });
        Elements links = document.getElementsByTag("link");
        links.forEach(element -> {
            String href = element.attr("href");
            if (!href.contains("http")) {
                element.attr("href", baseUrl + href);
            }
        });
        return document;
    }
}
