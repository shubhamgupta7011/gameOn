package com.example.GameOn.service;

import com.example.GameOn.utils.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RedisService {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private final ReactiveValueOperations<String, String> valueOps;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisService(ReactiveMongoTemplate mongoTemplate, ReactiveValueOperations<String, String> valueOps) {
        this.valueOps = valueOps;
    }

    public Mono<Boolean> deleteCache(String key){
        return redisTemplate.opsForValue().delete(key);
    }

    private <T> void cacheAList(String key, List<T> list) {
        if (!list.isEmpty()) {
            try {
                redisTemplate.opsForValue()
                        .set(key, objectMapper.writeValueAsString(list))
                        .subscribe();
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize data", e);
            }
        }
    }

    public <T> Mono<T> updateCache(String key, T savedEntry){
        return  redisTemplate.opsForValue().delete(key)       // Invalidate cache
                .then(Mono.fromCallable(() -> objectMapper.writeValueAsString(savedEntry))) // Serialize new data
                .flatMap(json -> redisTemplate.opsForValue().set(key, json))  // Update cache
                .thenReturn(savedEntry);
    }

    public <T> Mono<Boolean> saveNewCache(String key, T savedEntry){
        return  redisTemplate.opsForValue()
                .set(key, Utility.serialize(savedEntry));


    }


}
