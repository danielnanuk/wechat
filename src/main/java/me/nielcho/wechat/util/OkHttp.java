package me.nielcho.wechat.util;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class OkHttp {

    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(100, 60, TimeUnit.SECONDS))
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build();


    public static String doRequest(Request request, Consumer<Map<String, String>> cookieConsumer) {
        try {

            Response response = doGetResponse(request);
            if (cookieConsumer != null) {
                Headers headers = response.headers();
                List<String> setCookies = headers.values("Set-Cookie");
                if (!CollectionUtils.isEmpty(setCookies)) {
                    Map<String, String> cookieMap = new HashMap<>(setCookies.size());
                    setCookies.forEach(c -> {
                        int eq = c.indexOf("=");
                        String key = c.substring(0, eq);
                        String value = c.substring(eq + 1, c.indexOf(";"));
                        cookieMap.put(key, value);
                    });
                    cookieConsumer.accept(cookieMap);
                }
            }
            return response.body().string();
        } catch (IOException e) {
            log.error("error request: {}:{}, e:{}", request.method(), request.url(), e);
            return null;
        }

    }

    public static Response doGetResponse(Request request) throws IOException {
        return client.newCall(request).execute();
    }

    public static <T> T doRequest(Request request, Class<T> clazz, Consumer<Map<String, String>> cookieConsumer) {
        String result = doRequest(request, cookieConsumer);
        try {
            if (StringUtils.isEmpty(result)) {
                log.warn("null response for {}:{}" + request.method(), request.url());
                return null;
            }
            return JSON.parseObject(result, clazz);
        }catch (Exception e) {
            log.error("error parse json:" + result, e);
            throw  e;
        }
    }
}
