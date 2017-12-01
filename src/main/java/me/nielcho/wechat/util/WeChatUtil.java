package me.nielcho.wechat.util;


import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeChatUtil {

    private static char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * 生成16位微信设备码
     * 如: e729304849102334, 数字首位不为0
     */
    public static String generateDeviceID() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        int i = 0;
        builder.append(DIGITS[random.nextInt(9) + 1]);
        while (i < 14) {
            i++;
            builder.append(DIGITS[random.nextInt(10)]);
        }
        return "e" + builder.toString();
    }

    public static String getRandomNumber(int length) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        Random random = new Random();
        while (i++ < length) {
            builder.append(DIGITS[random.nextInt(10)]);
        }
        return builder.toString();
    }

    /**
     * 生成本地消息ID
     */
    public static String generateClientMsgId() {
        return System.currentTimeMillis() / 1000 + "" + getRandomNumber(4);
    }

    /**
     * 生成本地媒体ID
     */
    public static long generateClientMediaId() {
        return (System.currentTimeMillis() / 1000) * 1000 + new Random().nextInt(10000);
    }

    /**
     * 获取文件后缀名
     */
    public static String getFileExt(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(dotIndex) : "";
    }

    public static double getPGVPVI() {
        return (Math.round(Integer.MAX_VALUE * Math.random()) * (double) System.currentTimeMillis()) % 10000000000.;
    }

    public static Map<String, String> getQueryString(String url) {
        if (!url.contains("?")) {
            return Collections.emptyMap();
        }
        int idx = url.indexOf("?");
        String param = url.substring(idx + 1);
        String[] keyValuePair = param.split("&");
        Map<String, String> map = new HashMap<>();
        for (String kv : keyValuePair) {
            int equalIdx =  kv.indexOf("=");
            if (equalIdx != kv.length() - 1) {
                map.put(kv.substring(0,equalIdx), kv.substring(equalIdx+1));
            }
        }
        return map;
    }

    public static List<String> match(Pattern pattern, String input) {
        if (StringUtils.isEmpty(input)) return Collections.emptyList();
        Matcher matcher = pattern.matcher(input);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                result.add(matcher.group(i + 1));
            }
        }
        return result;
    }

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


    public static long invertLong(long n) {
        return ((~(int)(n >> 8)) << 8) & ~(int)(n);
    }
}
