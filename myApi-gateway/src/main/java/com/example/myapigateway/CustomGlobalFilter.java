package com.example.myapigateway;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import com.example.myApicommon.model.entity.InterfaceInfo;
import com.example.myApicommon.model.entity.User;
import com.example.myApicommon.service.InnerInterfaceInfoService;
import com.example.myApicommon.service.InnerUserInterfaceInfoService;
import com.example.myApicommon.service.InnerUserService;
import com.example.myapiclientsdk.utils.SignUtils;
import com.fasterxml.jackson.core.JsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component

public class CustomGlobalFilter implements GlobalFilter, Ordered {
    @DubboReference
    private InnerUserService innerUserService;
    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;


    //白名单常量
    public static final List<String> WHITE_LIST = Arrays.asList("127.0.0.1");
    public static final String INTERFACE_HOST="http://localhost:8123";



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        业务逻辑
//    1.用户发送请求到API网关
//        2.请求日志
//        3.黑白名单（）
//        4.用户鉴权
//        5.请求的模拟接口是否存在
//        6请求转发  调用模拟接口
//        7响应日志
//        8 调用成功   接口调用次数+1
//        9 调用失败  返回一个规范的错误码
//exchange(路由交换机)：**我们所有的请求的信息、响应的信息、响应体、请求体都能从这里拿到。
//**chain(责任链模式)：**因为我们的所有过滤器是按照从上到下的顺序依次执行，形成了一个链条。所以这里用了一个chain，如果当前过滤器对请求进行了过滤后发现可以放行，就要调用责任链中的next方法，
// 相当于直接找到下一个过滤器，这里称为filter。有时候我们需要在责任链中使用 next，而在这里它使用了 filter 来找到下一个过滤器，从而正常地放行请求。
// 2.请求日志
        ServerHttpRequest request = exchange.getRequest();
        String url = INTERFACE_HOST + request.getPath().value();
        System.out.println("请求唯一标识" + request.getId());
        //后面要查询接口信息是否存在  接口信息就是从请求路径中得到的
        System.out.println("请求路径" + url);
        System.out.println("请求方法" + request.getMethod());
        System.out.println("请求参数" + request.getQueryParams());
        String hostString = request.getLocalAddress().getHostString();
        System.out.println("请求来源主机" + hostString);
        System.out.println("请求来源地址" + request.getRemoteAddress());
        //        3.黑白名单（）   把响应对象直接状态码403禁止访问
        ServerHttpResponse response = exchange.getResponse();
        System.out.println("响应对象" + response);
        if (!WHITE_LIST.contains(hostString)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            //返回处理完成的响应，
            //            确认无需进一步写入响应：确保不会向响应中添加更多的数据，比如在发送完数据流后。
            //            关闭HTTP连接：在非保持连接（non-keep-alive）的HTTP请求处理完毕后，可以关闭连接。
            //            资源清理：确保所有的资源，比如数据库连接或文件流，都在发送完毕后正确关闭和清理。
            //            类似于前端的Promise就好理解了  async  await
            return response.setComplete();
        }
//3 用户鉴权
        //获取请求头中携带的参数   nonce随机数  时间戳timestamp body sign签名 accessKey
        HttpHeaders httpHeaders = request.getHeaders();
        String accessKey = httpHeaders.getFirst("accessKey");
        String nonce = httpHeaders.getFirst("nonce");
        String timestamp = httpHeaders.getFirst("timestamp");
        String body = httpHeaders.getFirst("body");
        String oldSign = httpHeaders.getFirst("sign");
        //done 去数据库获取accessKey
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);

        }catch (Exception e ){
            log.error("getInvokeUser 失败",e);;
        }
        if (invokeUser == null){
            return handleNoAuth(response);
        }
        //通过上面的代码替代
//        if (!"liu".equals(accessKey)) {
//            return handleNoAuth(response);
//        }
        // 校验随机数，模拟一下，直接判断nonce是否大于10000
        if (Long.parseLong(nonce) > 10000) {
            return handleNoAuth(response);
        }
        // 首先,获取当前时间的时间戳,以秒为单位
        // System.currentTimeMillis()返回当前时间的毫秒数，除以1000后得到当前时间的秒数。
        Long currentTime = System.currentTimeMillis() / 1000;
        // 定义一个常量FIVE_MINUTES,表示五分钟的时间间隔(乘以60,将分钟转换为秒,得到五分钟的时间间隔)。
        final Long FIVE_MINUTES = 60 * 5L;
        // 判断当前时间与传入的时间戳是否相差五分钟或以上
        // Long.parseLong(timestamp)将传入的时间戳转换成长整型
        // 然后计算当前时间与传入时间戳之间的差值(以秒为单位),如果差值大于等于五分钟,则返回true,否则返回false
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            // 如果时间戳与当前时间相差五分钟或以上，调用handleNoAuth(response)方法进行处理
            return handleNoAuth(response);
        }
        Map<String, String> hashmap = new HashMap<>();
        hashmap.put("accessKey", accessKey);
        hashmap.put("nonce", nonce);
        hashmap.put("timestamp", timestamp);
        hashmap.put("body", body);
// done
// 同样的方式进行检验,这里的secretKey在实际情况中应该是从数据库中获取的
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtils.getSign(hashmap, secretKey);

        if (oldSign==null||!oldSign.equals(serverSign)) {
            return handleNoAuth(response);
        }
// 4.  判断请求的模拟接口是否可用 因为在项目中并没有引入数据库的依赖 比如mybatis 可能会重复
        //todo 从数据库中查询模拟接口是否存在  以及请求方法是否匹配
        InterfaceInfo  interfaceInfo = null;

        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(url, request.getMethodValue());
        }catch (Exception e){
            log.info("查询数据库接口信息失败");
        }
        //检查是否成功拿到
        if (interfaceInfo == null){
            return handleNoAuth(response);
        }
// 5 请求转发  调用模拟接口
//        这一行代码负责将当前的HTTP请求（封装在 ServerWebExchange exchange 中）
//        传递到过滤器链中的下一个过滤器或

//        在过滤器的上下文中，Mono<Void> 表示过滤操作的完成状态：
//        Mono<Void> filter = chain.filter(exchange);

//        6 7 和5 的先后关系已经在装饰着模式中体现
//6 响应日志
//        log.info("响应："+response.getStatusCode());
//       对应了响应成功之后的操作  放去了处理响应的writewith方法中
//        if (response.getStatusCode() == HttpStatus.OK){
// 7todo 调用成功 接口调用次数+1  AOP
//            return filter;
//        }else {
//            return handleInvokeError(response);
//        }
        return handleResponse(exchange, chain, interfaceInfo.getId(),invokeUser.getId());
    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain,long interfaceInfoId,long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行,这里是重点  debug 的时候会先调用接口再回来执行这个
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        //log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 7. 调用成功，接口调用次数 + 1 invokeCount
                                        // todo 3接口调用次数+1  AOP
                                        try{
                                            innerUserInterfaceInfoService.invokeCount(interfaceInfoId,userId);
                                        }catch (Exception e ){
                                            log.info("接口调用次数统计失败");
                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }


    public Mono<Void> handleNoAuth(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.FORBIDDEN);
        //todo   返回值可以添加额外的信息
        return  response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        //todo   返回值可以添加额外的信息
        return  response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

