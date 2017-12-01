package me.nielcho.wechat.config;

import me.nielcho.wechat.manager.WeChatManager;
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
        executor.setQueueCapacity(0);
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(500);
        executor.setThreadNamePrefix("WeChat");
        executor.setKeepAliveSeconds(10000);
        executor.initialize();
        return executor;
    }
}
