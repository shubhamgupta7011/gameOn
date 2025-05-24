package com.example.GameOn.service;

import com.example.GameOn.entity.Feedback;
import com.example.GameOn.entity.PlansAndOffers;
import com.example.GameOn.entity.Ratings;
import com.example.GameOn.entity.Venue;
import com.example.GameOn.filters.QueryBuilder;
import com.example.GameOn.repository.PlansRepository;
import com.example.GameOn.repository.VenueRepository;
import com.example.GameOn.utils.Utility;
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
public class PlansService {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String KEY_PREFIX = "plans::";
    private static final String ALL_KEY_PREFIX = "all_plans";
    //    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();

    @Autowired
    PlansRepository repository;


    private final ReactiveMongoTemplate mongoTemplate;

    public PlansService(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Mono<PlansAndOffers> save(PlansAndOffers myEntry){
        myEntry.setLastUpdatedOn(Utility.getCurrentTime());
        return repository.save(myEntry)
                .flatMap(savedEntity -> {
                    String key = KEY_PREFIX + savedEntity.getId();
                    return redisTemplate.opsForValue().delete(key)       // Invalidate cache
                            .then(Mono.fromCallable(() -> objectMapper.writeValueAsString(savedEntity))) // Serialize new data
                            .flatMap(json -> redisTemplate.opsForValue().set(key, json))  // Update cache
                            .thenReturn(savedEntity);
                });
    }

    public Mono<PlansAndOffers> saveNew(PlansAndOffers myEntry){
//        user.setPassword(passwordEn.encode(user.getPassword()));
        myEntry.setCreatedOn(Utility.getCurrentTime());
        myEntry.setLastUpdatedOn(Utility.getCurrentTime());
        return repository.save(myEntry)
                .doOnNext(savedEntry -> redisTemplate.opsForValue()
                        .set(KEY_PREFIX + savedEntry.getId(), Utility.serialize(savedEntry))
                        .subscribe());
//        return repository.save(myEntry);
    }

    public Flux<PlansAndOffers> getFilteredList(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
        String key = Utility.generateCacheKey(ALL_KEY_PREFIX,filters,page,size,sortBy,sortOrder);
        Query query = QueryBuilder.buildQuery(filters, page, size, sortBy, sortOrder);

        return redisTemplate.opsForValue().get(key)
                .flatMapMany(cachedData -> Utility.deserializeCache(cachedData, PlansAndOffers[].class))
                .switchIfEmpty(Utility.fetchAndCacheFromDB(query, key, PlansAndOffers.class, mongoTemplate,redisTemplate));
    }

    public Mono<PlansAndOffers> getById(ObjectId id){
        String key = KEY_PREFIX + id.toHexString();

        return redisTemplate.opsForValue().get(key)
                .flatMap(json -> Mono.fromCallable(() -> objectMapper.readValue(json, PlansAndOffers.class)))
                .onErrorResume(e -> Mono.empty()) // if deserialization fails, ignore cache and load from DB
                .switchIfEmpty(
                        repository.findById(id).flatMap(entry -> Mono.fromCallable(() -> objectMapper.writeValueAsString(entry))
                                .flatMap(json -> redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(5)).thenReturn(entry)))
                );
    }

    public void delete(String id){
        String key = KEY_PREFIX + id;

        repository.deleteById(new ObjectId(id))
                .then(redisTemplate.opsForValue().delete(key))
                .subscribe();

//        repository.deleteById(new ObjectId(id));
    }

}
