package com.example.GameOn.config;

import com.example.GameOn.entity.Clubs;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.data.redis.serializer.*;
import reactor.core.publisher.Mono;

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
        System.out.println("🔄 Testing Redis Connection...");
        factory.getReactiveConnection()
                .ping()
                .doOnNext(pong -> {
                    if ("PONG".equals(pong)) {
                        System.out.println("✅ Redis Connection Established Successfully!");
                    } else {
                        System.out.println("❌ Redis Connection Failed!");
                    }
                })
                .doOnError(error -> System.out.println("❌ Redis Connection Error: " + error.getMessage()))
                .subscribe();
    }
}
