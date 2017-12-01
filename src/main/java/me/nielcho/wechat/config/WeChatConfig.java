package me.nielcho.wechat.config;

import me.nielcho.wechat.manager.WeChatManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ConfigurationProperties(prefix = "swagger")
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

    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("me.nielcho.wechat"))
                .paths(PathSelectors.ant("/apis/**"))
                .build();
    }


    private ApiInfo apiInfo() {
        return new ApiInfo(
                "微信网页版API",
                "微信网页版API",
                "v1",
                "termsOfService",
                "danielnanuk@gmail.com",
                "N/A",
                "N/A"
        );

    }
}
