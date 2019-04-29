package com.tc.reptile.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    private static final RestTemplate restTemplate = new RestTemplate();

    /***
     * @Author: Chensr
     * @Description: 调用链接获取数据，返回data数组
     * @Date: 2019/3/30 14:45
     * @param url
     * @param param
     * @return: java.util.Optional<com.alibaba.fastjson.JSONArray>
     */
    public static Optional<Object> getDataForJson(String url) {
        String response = restTemplate.getForObject(url, String.class);
        Object data = getResponseData(response);
        return data == null ? Optional.empty() : Optional.of(data);

    }

    /***
     * @Author: Chensr
     * @Description: 调用链接获取数据，返回data数组
     * @Date: 2019/3/30 14:45
     * @param url
     * @param param
     * @return: java.util.Optional<com.alibaba.fastjson.JSONArray>
     */
    public static Optional<JSONArray> getDataForJson(String url, Map<String, Object> param) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        param.forEach((k, v) -> {
            builder.queryParam(k, v);
        });
        String response = restTemplate.getForObject(builder.build().encode().toUri(), String.class);
        Object data = getResponseData(response);
        return data == null ? Optional.empty() : Optional.of((JSONArray) data);

    }

    /***
     * @Author: Chensr
     * @Description: 调用链接获取数据，返回data数组
     * @Date: 2019/3/30 14:45
     * @param url
     * @param param
     * @return: java.util.Optional<com.alibaba.fastjson.JSONArray>
     */
    public static Optional<Object> getDataForJson(String url, HttpHeaders httpHeaders, Map<String, Object> param) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        param.forEach((k, v) -> {
            builder.queryParam(k, v);
        });

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<MultiValueMap<String,String>>(null,httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, request, String.class);
        Object data = getResponseData(responseEntity.getBody());
        return data == null ? Optional.empty() : Optional.of(data);

    }

    private static Object getResponseData(String response) {
        // 解析数据保存数据
        JSONObject json = JSONObject.parseObject(response);
       return json.get("data");
    }

    /***
     * @Author: Chensr
     * @Description: 调用链接获取数据，返回data数组
     * @Date: 2019/3/30 14:45
     * @param url
     * @param param
     * @return: java.util.Optional<com.alibaba.fastjson.JSONArray>
     */
    public static Optional<JSONObject> postDataForJson(String url, Map<String, String> param) {
        MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            requestEntity.add(entry.getKey(), entry.getValue());
        }
        String response = restTemplate.postForObject(url, requestEntity, String.class);

        // 解析数据保存数据
        Object data = getResponseData(response);
        return data == null ? Optional.empty() : Optional.of((JSONObject) data);
    }

    /***
     * @Author: Chensr
     * @Description: 获取html页面的document
     * @Date: 2019/3/30 16:34
     * @param url
     * @param baseUrl
     * @return: org.jsoup.nodes.Document
     */
    public static Document getYysArticleDocument(String url, String baseUrl){
        Document document = getDocument(url);

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

    /**
     * 获取html页面的document
     * @param url
     * @return
     */
    public static Document getDocument(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return document;
    }

    public static void main(String[] args) {
        String forObject = restTemplate.getForObject("https://click.gamersky.com/Common/GetHits.aspx?id=1178882&script=3", String.class);
        System.out.println();
    }
}
