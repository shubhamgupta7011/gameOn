package com.example.GameOn.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.data.redis.serializer.*;

@Slf4j
@Configuration
public class RedisConfig {

    private final ReactiveRedisConnectionFactory factory;

    // Use Spring Boot's auto-configured RedisConnectionFactory
    public RedisConfig(ReactiveRedisConnectionFactory factory) {
        this.factory = factory;
    }

    @Bean
    @Primary
    public ReactiveRedisTemplate<String, String> stringReactiveRedisTemplate() {

        // Use String serializer for both key and value
        RedisSerializationContext<String, String> context = RedisSerializationContext
                .<String, String>newSerializationContext(new StringRedisSerializer())
                .key(new StringRedisSerializer())
                .value(new StringRedisSerializer())
                .hashKey(new StringRedisSerializer())
                .hashValue(new StringRedisSerializer())
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveValueOperations<String, String> reactiveValueOperations(ReactiveRedisTemplate<String, String> template) {
        return template.opsForValue();
    }

    @PostConstruct
    public void testRedisConnection() {
        log.info("🔄 Testing Redis Connection...");
        factory.getReactiveConnection()
                .ping()
                .doOnNext(pong -> {
                    if ("PONG".equals(pong)) {
                        log.info("✅ Redis Connection Established Successfully!");
                    } else {
                        log.error("❌ Redis Connection Failed!");
                    }
                })
                .doOnError(error -> log.error("❌ Redis Connection Error: " + error.getMessage()))
                .subscribe();
    }
}
