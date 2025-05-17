package com.example.GameOn.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
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
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate() {
        RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext
                .<String, Object>newSerializationContext(new StringRedisSerializer())
                .key(new StringRedisSerializer())
//                .value(new StringRedisSerializer())
                .value(new GenericJackson2JsonRedisSerializer())
                .hashKey(new StringRedisSerializer())
                .hashValue(new GenericJackson2JsonRedisSerializer())
                .build();
        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }

//    @Bean
//    @Primary
//    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate() {
//        RedisSerializationContext<String, String> serializationContext = RedisSerializationContext
//                .<String, String>newSerializationContext(new StringRedisSerializer())
//                .key(new StringRedisSerializer())
//                .value(new StringRedisSerializer())
////                .value(new GenericJackson2JsonRedisSerializer())
//                .hashKey(new StringRedisSerializer())
//                .hashValue(new GenericJackson2JsonRedisSerializer())
//                .build();
//        return new ReactiveRedisTemplate<>(factory, serializationContext);
//    }

    @Bean
    public ReactiveValueOperations<String, Object> reactiveValueOperations(ReactiveRedisTemplate<String, Object> template) {
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
