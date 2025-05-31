package com.example.GameOn.service;

import com.example.GameOn.entity.Ratings;
import com.example.GameOn.entity.UserDetails.MatchPreference;
import com.example.GameOn.filters.QueryBuilder;
import com.example.GameOn.repository.MatchPrefrenceRepository;
import com.example.GameOn.repository.RatingRepository;
import com.example.GameOn.repository.UserRepository;
import com.example.GameOn.utils.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
public class MatchPrefrenceService {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String KEY_PREFIX = "match::";
    //    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();

    @Autowired
    MatchPrefrenceRepository repository;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    public Mono<MatchPreference> save(MatchPreference entry) {
        return repository.save(entry).flatMap(savedEntity -> {
            String key = KEY_PREFIX + savedEntity.getUserId();
            return redisTemplate.opsForValue().delete(key)       // Invalidate cache
                    .then(Mono.fromCallable(() -> objectMapper.writeValueAsString(savedEntity))) // Serialize new data
                    .flatMap(json -> redisTemplate.opsForValue().set(key, json))  // Update cache
                    .thenReturn(savedEntity);
        });
    }

    public Mono<MatchPreference> saveNew(MatchPreference myEntry) {
        myEntry.setCreatedOn(Utility.getCurrentTime());
        myEntry.setLastUpdatedOn(Utility.getCurrentTime());
        return repository.save(myEntry)
                .doOnNext(savedEntry -> redisTemplate.opsForValue()
                        .set(KEY_PREFIX + savedEntry.getUserId(), Utility.serialize(savedEntry))
                        .subscribe());
    }

    public Mono<MatchPreference> getByUserId(String uId) {
        String key = KEY_PREFIX + uId;

        return redisTemplate.opsForValue().get(key)
                .flatMap(json -> Mono.fromCallable(() -> objectMapper.readValue(json, MatchPreference.class)))
                .onErrorResume(e -> Mono.empty()) // if deserialization fails, ignore cache and load from DB
                .switchIfEmpty(
                        repository.findByUserId(uId).flatMap(entry -> Mono.fromCallable(() -> objectMapper.writeValueAsString(entry))
                                .flatMap(json -> redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(5)).thenReturn(entry)))
                );
    }
}
