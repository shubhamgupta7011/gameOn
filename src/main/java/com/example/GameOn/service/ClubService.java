package com.example.GameOn.service;

import com.example.GameOn.entity.Booking;
import com.example.GameOn.entity.Clubs;
import com.example.GameOn.repository.ClubRepository;
import com.example.GameOn.utils.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Component
public class ClubService {

    @Autowired
    ClubRepository repository;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private final ReactiveValueOperations<String, String> valueOps;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String KEY_PREFIX = "club::";
    private static final String ALL_CLUBS_KEY = "all_clubs";
    //    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();
    private final ReactiveMongoTemplate mongoTemplate;

    public ClubService(ReactiveMongoTemplate mongoTemplate, ReactiveValueOperations<String, String> valueOps) {
        this.mongoTemplate = mongoTemplate;
        this.valueOps = valueOps;
    }

    public Mono<Clubs> save(Clubs myEntry){
        myEntry.setLastUpdatedOn(Utility.getCurrentTime());

        return repository.save(myEntry)
                .flatMap(savedClub -> {
                    String key = KEY_PREFIX + savedClub.getId();
                    return redisTemplate.opsForValue().delete(key)       // Invalidate cache
                            .then(Mono.fromCallable(() -> objectMapper.writeValueAsString(savedClub))) // Serialize new data
                            .flatMap(json -> redisTemplate.opsForValue().set(key, json))  // Update cache
                            .thenReturn(savedClub);
                });
    }

    public Mono<Clubs> saveNew(Clubs myEntry){
//        user.setPassword(passwordEn.encode(user.getPassword()));
        myEntry.setCreatedOn(Utility.getCurrentTime());
        myEntry.setLastUpdatedOn(Utility.getCurrentTime());
        return repository.save(myEntry)
                .doOnNext(savedEntry -> redisTemplate.opsForValue()
                        .set(KEY_PREFIX + savedEntry.getId(), Utility.serialize(savedEntry))
                        .subscribe());
    }

    public Flux<Clubs> getFilteredList(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
        Query query = new Query();

        // ✅ Apply dynamic filters
        if (filters != null && !filters.isEmpty()) {
            filters.forEach((key, value) -> query.addCriteria(Criteria.where(key).is(value)));
        }

        // ✅ Sorting
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            query.with(Sort.by(direction, sortBy));
        }

        // ✅ Pagination
        query.skip((long) page * size).limit(size);

        return mongoTemplate.find(query, Clubs.class);
    }

    public Mono<Clubs> getById(ObjectId id) {
        String key = KEY_PREFIX + id.toHexString();

        return redisTemplate.opsForValue().get(key)
                .flatMap(json -> Mono.fromCallable(() -> objectMapper.readValue(json, Clubs.class)))
                .onErrorResume(e -> Mono.empty()) // if deserialization fails, ignore cache and load from DB
                .switchIfEmpty(
                        repository.findById(id).flatMap(entity -> Mono.fromCallable(() -> objectMapper.writeValueAsString(entity))
                                .flatMap(json -> redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(5)).thenReturn(entity)))
                );
    }

    public void delete(String id){
        String key = KEY_PREFIX + id;

        repository.deleteById(new ObjectId(id))
                .then(redisTemplate.opsForValue().delete(key))
                .subscribe();
    }

}
