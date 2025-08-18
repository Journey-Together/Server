package Journey.Together.global.config;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Logger;
import feign.QueryMapEncoder;
import feign.Retryer;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import feign.codec.Encoder;
import feign.querymap.BeanQueryMapEncoder;

@Configuration
@EnableFeignClients(basePackages = "Journey.Together.global.external")
public class OpenFeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }


    @Bean
    public Encoder feignEncoder() {
        return new SpringEncoder(HttpMessageConverters::new);
    }

    @Bean
    public QueryMapEncoder queryMapEncoder() {
        return new BeanQueryMapEncoder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        return objectMapper;
    }

    @Bean
    public Decoder feignDecoder() {
        return new JacksonDecoder(objectMapper());
    }

    @Bean
    public Retryer feignRetryer() {
        // period = 100ms, maxPeriod = 1s, maxAttempts = 3
        return new Retryer.Default(1000, 10000, 3);
    }

}
