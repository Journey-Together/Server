package Journey.Together.global.config;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import feign.Logger;
import feign.QueryMapEncoder;
import feign.Retryer;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import feign.codec.Encoder;
import feign.jackson.JacksonEncoder;
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
        objectMapper.registerModule(new JavaTimeModule()); // LocalDate, LocalDateTime 지원
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO 포맷으로
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
