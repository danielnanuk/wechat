package me.nielcho.wechat.util;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class WeChatUtil {

    private static char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * 生成16位设备id
     *
     * @return 16位微信设备码
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

    /**
     * 用数字生成随机字符串,可以0开始
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
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
     * unicode to utf8
     *
     * @param string string may contains unicode
     * @return converted string
     */
    public static String unicodeToUtf8(String string) {
        try {
            if (!string.contains("\\u")) {
                return string;
            }
            byte[] utf8 = string.getBytes("UTF-8");
            string = new String(utf8, "UTF-8");
            return string;
        } catch (UnsupportedEncodingException e) {
            // unreachable
        }
        return string;
    }

    public static String generateClientMsgId() {
        return System.currentTimeMillis() / 1000 + "" + WeChatUtil.getRandomNumber(4);
    }

    public static long generateClientMediaId() {
        return (System.currentTimeMillis() / 1000) * 1000 + new Random().nextInt(10000);
    }

    public static String getFileExt(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(dotIndex) : "";
    }

    private static final List<String> USER_AGENTS = Arrays.asList("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:53.0) Gecko/20100101 Firefox/53.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.79 Safari/537.36 Edge/14.14393",
            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; Trident/5.0;  Trident/5.0)",
            "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0; MDDCJS)",
            "Mozilla/5.0 (compatible, MSIE 11, Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko");

    public static String getRandomUserAgent() {
        return USER_AGENTS.get(ThreadLocalRandom.current().nextInt(USER_AGENTS.size()));
    }

    public static double getPGVPVI() {
        return (Math.round(Integer.MAX_VALUE * Math.random()) * (double) System.currentTimeMillis()) % 10000000000.;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getQueryString(String url) {
        if (!url.contains("?")) {
            return Collections.emptyMap();
        }

        int idx = url.indexOf("?");
        String param = url.substring(idx + 1);
        String[] keyValuePair = param.split("&");
        Map<String, String> map = new HashMap<>();
        for (String kv : keyValuePair) {
            int equalIdx = kv.indexOf("=");
            if (equalIdx == kv.length() - 1) {
                map.put(kv.substring(0, equalIdx), null);
            } else {
                map.put(kv.substring(0, equalIdx), kv.substring(equalIdx + 1));
            }
        }
        return map;
    }

    public static long invertLong(long n) {
        return ((~(int)(n >> 8)) << 8) & ~(int)(n);
    }


}
