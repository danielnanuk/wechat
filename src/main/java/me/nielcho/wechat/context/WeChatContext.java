package me.nielcho.wechat.context;

import lombok.Data;
import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.request.BaseRequest;
import me.nielcho.wechat.response.SyncKey;
import me.nielcho.wechat.response.WeChatUserResponse;

import java.util.HashMap;
import java.util.Map;


@Data
public class WeChatContext {

    // 会话id
    private String sessionId;

    // 是否已扫码
    private String tip;

    // 当次登录微信标示
    private String uuid;

    private String skey;

    private String sid;

    private String passTicket;

    private String uin;

    private String deviceId;

    private Map<String, String> cookies = new HashMap<>();

    private SyncKey syncKey;

    private WeChatUserResponse user;

    private BaseRequest baseRequest;

    private String state;

    private String avatar;

    private String domain;

    private String pushDomain;

    private String fileDomain;

    // 该上下文在redis中最后刷新时间
    private Long lastRefreshTime;

    private String userAgent;

    private String host;

    private Integer employeeId;

    private String employeeName;

    private String message;

    /**
     * 以下属性与业务无关, 为模拟微信埋点属性
     */
    private Long qrcodeStart;
    private Long qrcodeEnd;
    private Long scan;
    private Long loginEnd;
    private Long initStart;
    private Long initEnd;
    private Long initContactStart;

    private int logoutCode;

    public static boolean isLogin(WeChatContext context) {
        return context != null && WeChatConstants.CONTEXT_STATE_LOGINED.equals(context.getState());
    }

}
