package com.example.myapiclientsdk;

import com.example.myapiclientsdk.client.MyAPIClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
//@ComponentScan 注解用于自动扫描组件，使得 Spring 能够自动注册相应的 Bean
@ComponentScan
//ConfigurationProperties能够读取application.yml的配置,读取到配置之后,把这个读到的配置设置到我们这里的属性中,
//// 这里给所有的配置加上前缀为"myapi.client"
@ConfigurationProperties(prefix = "myapi.client")
public class myApiClientConfig {
    private String accessKey;
    private String secretKey;

    //现在就是通过读取这个配置拿到这两个值。用这两个值去得到这样一个客户端。
    @Bean
    public MyAPIClient myApiClient() {
        return
                new MyAPIClient(accessKey, secretKey);
    }
}
