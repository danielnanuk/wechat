package me.nielcho.wechat.manager;

import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.session.WeChatSession;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class WeChatManager {

    private static ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private static String WX_REQUEST_CONST = "WX_REQ_KEY";

    @Resource(name = "weChatSessionExecutor")
    public void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        WeChatManager.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    private static Map<String, WeChatSession> SESSION_MAP = new ConcurrentHashMap<>();
    private static Map<String, WeChatContext> CONTEXT_MAP = new ConcurrentHashMap<>();


    private static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }


    public static WeChatSession getWeChatSession(String id) {
        return SESSION_MAP.get(id);
    }

    public static WeChatContext getWeChatContext(String id) {
        return CONTEXT_MAP.get(id);
    }

    public static WeChatContext getContext() {
        HttpServletRequest request = getRequest();
        return getWeChatContext(String.valueOf(request.getSession().getAttribute(WX_REQUEST_CONST)));


    }

}