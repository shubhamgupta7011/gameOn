package com.example.GameOn.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class Utility {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Long getCurrentTime() {
        return LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
                .toInstant(ZoneId.of("UTC").getRules().getOffset(Instant.now()))
                .toEpochMilli();
    }

    public static String generateCacheKey(String key, Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
        String filterString = filters.toString();
        return key + filterString + ":" + page + ":" + size + ":" + sortBy + ":" + sortOrder;
    }

    public static <T> Flux<T> deserializeList(String json, Class<T> clazz) {
        try {
            List<T> list = objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
            return Flux.fromIterable(list);
        } catch (Exception e) {
            return Flux.error(new RuntimeException("Failed to deserialize cached data", e));
        }
    }

    public static <T> Flux<T> deserializeCache(String cachedData, Class<T[]> type) {
        try {
            return Flux.fromArray(objectMapper.readValue(cachedData, type));
        } catch (Exception e) {
            log.error("Failed to deserialize cache data: {}", e.getMessage());
            return Flux.empty();
        }
    }

    public static <T> Mono<T> deserialize(String json, Class<T> clazz) {
        return Mono.fromCallable(() -> objectMapper.readValue(json, clazz));
    }

    public static <T> Flux<T> deserializeToFlux(String json, Class<T[]> type) {
        try {
            T[] array = objectMapper.readValue(json, type);
            return Flux.fromArray(array);
        } catch (Exception e) {
            return Flux.empty();
        }
    }

    public static <T> Mono<T> deserialize(String json, TypeReference<T> typeRef) {
        return Mono.fromCallable(() -> objectMapper.readValue(json, typeRef));
    }

    public static <T> String serialize(T club) {
        try {
            return objectMapper.writeValueAsString(club);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing object", e);
        }
    }

    public static <T> Mono<String> serializeMono(T value) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(value));
    }

    public static <T> void cacheItems(String key, List<T> items, ReactiveRedisTemplate<String, String> redisTemplate) {
        try {
            String json = serialize(items);
            redisTemplate.opsForValue().set(key, json,Duration.ofMinutes(15)).subscribe(
                    success -> {
                        if (success) log.info("Data cached successfully with key: {}", key);
                    },
                    error -> log.error("Failed to cache data: {}", error.getMessage())
            );
        } catch (Exception e) {
            log.error("Failed to serialize data for caching: {}", e.getMessage());
        }
    }

    public static <T> void cacheItem(String key, T items, ReactiveRedisTemplate<String, String> redisTemplate) {
        try {
            String json = serialize(items);
            redisTemplate.opsForValue().set(key, json).subscribe(
                    success -> {
                        if (success) log.info("Data cached successfully with key: {}", key);
                    },
                    error -> log.error("Failed to cache data: {}", error.getMessage())
            );
        } catch (Exception e) {
            log.error("Failed to serialize data for caching: {}", e.getMessage());
        }
    }

    public static <T> Flux<T> fetchAndCacheFromDB(Query query, String key, Class<T> entityClass, ReactiveMongoTemplate mongoTemplate, ReactiveRedisTemplate<String, String> redisTemplate) {
        return mongoTemplate.find(query, entityClass).collectList()
                .flatMapMany(items -> {
                    if (!items.isEmpty())
                        Utility.cacheItems(key, items, redisTemplate);

                    return Flux.fromIterable(items);
                });
    }

}
