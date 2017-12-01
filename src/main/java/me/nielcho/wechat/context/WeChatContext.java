package me.nielcho.wechat.context;

import lombok.Data;
import me.nielcho.wechat.request.BaseRequest;
import me.nielcho.wechat.response.SyncKey;
import me.nielcho.wechat.response.UserResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class WeChatContext {
    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
    private final Map<String, String> cookies = new HashMap<>();
    private String id;
    private String domain;
    private String host;
    private String tip;
    private String uuid;
    private String skey;
    private String fileDomain;
    private String sid;

    private String passTicket;

    private String uin;

    private String deviceId;
    private String avatar;
    private String pushDomain;

    private BaseRequest baseRequest;
    private UserResponse user;
    private SyncKey syncKey;
    private int state;
    private AtomicInteger mediaCount = new AtomicInteger(0);
}
