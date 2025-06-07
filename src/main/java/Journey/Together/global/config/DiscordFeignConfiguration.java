package Journey.Together.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.RequestInterceptor;

@Configuration
@EnableFeignClients(basePackages = "Journey.Together.global.external")
public class DiscordFeignConfiguration {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public RequestInterceptor discordRequestInterceptor() {
        return template -> {
            template.header("Content-Type", "application/json");
        };
    }
}
