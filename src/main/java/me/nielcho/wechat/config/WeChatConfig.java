package me.nielcho.wechat.config;

import me.nielcho.wechat.handler.*;
import me.nielcho.wechat.manager.WeChatManager;
import me.nielcho.wechat.repository.ContactRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class WeChatConfig {

    @Bean
    public WeChatManager weChatManager() {
        WeChatManager weChatManager = new WeChatManager();
        weChatManager.setThreadPoolTaskExecutor(weChatSessionExecutor());
        return weChatManager;
    }

    @Bean
    public ThreadPoolTaskExecutor weChatSessionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        ;
        // 线程池所使用的缓冲队列
        executor.setQueueCapacity(0);
        // 线程池维护线程的最少数量
        executor.setCorePoolSize(5);
        // 线程池维护线程的最大数量
        executor.setMaxPoolSize(500);
        executor.setThreadNamePrefix("WeChat");

        // 线程池维护线程所允许的空闲时间
        executor.setKeepAliveSeconds(10000);
        executor.initialize();
        return executor;
    }
}
