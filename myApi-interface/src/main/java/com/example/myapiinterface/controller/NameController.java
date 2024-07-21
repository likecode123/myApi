package com.example.myapiinterface.controller;


import com.example.myapiclientsdk.model.User;
import com.example.myapiclientsdk.utils.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("name")
public class NameController {
    @GetMapping("/")
    public  String getNameByGet(String name){
        return  "GET 你的名字是"+name;
    }


    @PostMapping("/")
    public  String getNameByPost(@RequestParam  String name){
        return  "Post 你的名字是"+name;
    }
//json传递  restful   接收json
    @PostMapping("/restful")
    public  String getUserNameByPost(@RequestBody User user, HttpServletRequest httpServletRequest){
//        获取到这些变量进行检验  这个地方偷懒没有检验完全
//        String accessKey = httpServletRequest.getHeader("accessKey");
//
//        String nonce = httpServletRequest.getHeader("nonce");
//        String timestamp = httpServletRequest.getHeader("timestamp");
//        String body = httpServletRequest.getHeader("body");
//        String oldSign = httpServletRequest.getHeader("sign");
//        if (!accessKey.equals("liu")){
//            throw new RuntimeException("无权限error");
//        }
        // 校验随机数，模拟一下，直接判断nonce是否大于10000
//        if (Long.parseLong(nonce) > 10000) {
//            throw new RuntimeException("无权限");
//        }
//        Map<String,String> hashmap = new HashMap<>();
//        hashmap.put("accessKey",accessKey);
//        hashmap.put("nonce",nonce);
//        hashmap.put("timestamp",timestamp);
//        hashmap.put("body",body);
//
//        //同样的方式进行检验,这里的secretKey在实际情况中应该是从数据库中获取的
//        String serverSign = SignUtils.getSign(hashmap, "abcde");
//        if(!oldSign.equals(serverSign)){
//         throw new RuntimeException("签名不一致");
//        }
        String result = "RESTFUL  Post 你的名字是"+user.getUsername();
        return  result;
    }

}
