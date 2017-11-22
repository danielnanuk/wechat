package me.nielcho.wechat.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;


public class URLUtils {
    
    public static String format(String url, Object... pathVariables) {
        Object[] params = new Object[pathVariables.length];
        for (int i = 0; i < params.length; i++) {
            try {
                params[i] = URLEncoder.encode(String.valueOf(pathVariables[i]), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return String.format(url, params);
    }
    
    public static String format(String url, Map<String, ?> params) {
        StringBuilder builder = new StringBuilder(url);
        builder.append("?");
        params.forEach((k, v) -> {
            try {
                builder.append(k).append("=").append(URLEncoder.encode(String.valueOf(v), "utf-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        return builder.toString();
    }
    
}