package me.nielcho.wechat.manager;

import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.session.WeChatSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;


public class WeChatManager {

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private ApplicationContext applicationContext;

    public WeChatContext getContext() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String sessionId = request.getSession().getId();
        return getSession(sessionId).getContext();
    }

    public void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    private ConcurrentHashMap<String, WeChatSession> sessionMap = new ConcurrentHashMap<>();

    public WeChatSession createSession(String id) {
        WeChatSession session = new WeChatSession(id);
        session.startLogin();
        String uuid = session.getUUID();
        if (StringUtils.isEmpty(uuid)) {
            return null;
        } else {
            sessionMap.put(id, session);
            initializeBean(session);
            threadPoolTaskExecutor.submit(session);
            return session;
        }
    }

    private void initializeBean(Object bean) {
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        beanFactory.autowireBean(bean);
        beanFactory.initializeBean(bean, bean.getClass().getName());
    }

    public WeChatSession getSession(String id) {
        return sessionMap.get(id);
    }


}
