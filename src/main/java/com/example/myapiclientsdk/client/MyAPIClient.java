package com.example.myapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.example.myapiclientsdk.model.User;


import java.util.HashMap;
import java.util.Map;

import static com.example.myapiclientsdk.utils.SignUtils.getSign;


/**
 * 客户端层，负责与用户交互、处理用户请求，以及调用服务端提供的 API 接口等任务的部分。
 */
public class MyAPIClient {

    private String accessKey;

    private String secretKey;

    public MyAPIClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }



    //get请求
    public  String getNameByGet(String name){
//可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result3= HttpUtil.get("http://localhost:8123/api/name/", paramMap);
        System.out.println(result3);
        return result3;
    }


//post请求
    public  String getNameByPost(String name){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result= HttpUtil.post("http://localhost:8123/api/name/", paramMap);
        return result;
    }


    // 创建一个私有方法，用于构造请求头
    private Map<String, String> getHeaderMap(String body) {
        // 创建一个新的 HashMap 对象
        Map<String, String> hashMap = new HashMap<>();
        // 将 "accessKey" 和其对应的值放入 map 中
        hashMap.put("accessKey", accessKey);

        // 将 "secretKey" 和其对应的值放入 map 中
//        hashMap.put("secretKey", "aab");

//获得一个只包含数字的字符串,设置长度
        hashMap.put("body", body);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));

        hashMap.put("sign", getSign(hashMap,secretKey));

        // 返回构造的请求头 map
        return hashMap;
    }



    //json传递  restful   接收json

    public  String getUserNameByPost( User user){
        String jsonRequest = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post("http://localhost:8123/api/name/restful/")
                .addHeaders(getHeaderMap(jsonRequest))
                .body(jsonRequest)
                .execute();
        // 打印服务器返回的状态码
        System.out.println(httpResponse.getStatus());
        // 获取服务器返回的结果
        String result = httpResponse.body();
        // 打印服务器返回的结果
        System.out.println(result);
        // 返回服务器返回的结果
        return result;
//        System.out.println("httpResponse的值"+httpResponse);
//        String bodyResult = httpResponse.body();
//        System.out.println("httpresponse的bady"+bodyResult);
//        return bodyResult;


    }



}
