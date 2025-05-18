package com.example.GameOn.utils;

import com.example.GameOn.entity.Amenity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Component
public class Utility {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Long getCurrentTime(){
        return LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
                .toInstant(ZoneId.of("UTC").getRules().getOffset(Instant.now()))
                .toEpochMilli();
    }

    public static  <T> Flux<T> deserializeList(String json, Class<T> clazz) {
        try {
            List<T> list = objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
            return Flux.fromIterable(list);
        } catch (Exception e) {
            return Flux.error(new RuntimeException("Failed to deserialize cached data", e));
        }
    }

    private Flux<Amenity> deserializeList(String json) {
        try {
            return Flux.fromArray(objectMapper.readValue(json, Amenity[].class));
        } catch (Exception e) {
            return Flux.error(new RuntimeException("Failed to deserialize cached data", e));
        }
    }

    public static <T> String serialize(T club) {
        try {
            return objectMapper.writeValueAsString(club);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing Club object", e);
        }
    }

    public static String generateCacheKey(String key, Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
        String filterString = filters.toString();
        return key + filterString + ":" + page + ":" + size + ":" + sortBy + ":" + sortOrder;
    }

}
