package com.example.myapiclientsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

import java.util.Map;

public class SignUtils {
    /**
     * 生成签名
     */

    public static  String getSign(Map<String,String> hashMap,String secretKey){
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        // 构建签名内容，将哈希映射转换为字符串并拼接密钥
        String testStr = hashMap.toString()+"."+secretKey;

        return md5.digestHex(testStr);

    }



}
