package com.example.GameOn.service;

import com.example.GameOn.entity.Amenity;
import com.example.GameOn.repository.AmenityRepository;
import com.example.GameOn.utils.QueryBuilder;
import com.example.GameOn.utils.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;


@Component
@Slf4j
public class AmenityService {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private final ReactiveValueOperations<String, String> valueOps;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String KEY_PREFIX = "amenity::";
    private static final String ALL_KEY_PREFIX = "all_amenities";
    //    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();

    @Autowired
    AmenityRepository repository;

    private final ReactiveMongoTemplate mongoTemplate;

    public AmenityService(ReactiveMongoTemplate mongoTemplate, ReactiveValueOperations<String, String> valueOps) {
        this.mongoTemplate = mongoTemplate;
        this.valueOps = valueOps;
    }

//    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();


    public Flux<Amenity> getFilteredList(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
        String key = Utility.generateCacheKey(KEY_PREFIX,filters, page, size, sortBy, sortOrder);
        Query query = QueryBuilder.buildQuery(filters, page, size, sortBy, sortOrder);
//        return mongoTemplate.find(query, Amenity.class);
        return redisTemplate.opsForValue()
                .get(key)
                .flatMapMany(cachedData -> {
                    try {
                        Amenity[] amenities = objectMapper.readValue(cachedData, Amenity[].class);
                        return Flux.fromArray(amenities);
                    } catch (Exception e) {
                        log.error("Failed to deserialize cache data: " + e.getMessage());
                        return Flux.empty();
                    }
                })
                .switchIfEmpty(
                        mongoTemplate.find(query, Amenity.class)
                                .collectList()
                                .flatMapMany(amenities -> {
                                    if (!amenities.isEmpty()) {
                                        try {
                                            String json = objectMapper.writeValueAsString(amenities);
                                            redisTemplate.opsForValue()
                                                    .set(key, json)
                                                    .subscribe(success -> {
                                                        if (success) {
                                                            log.info("Data cached successfully with key: " + key);
                                                        }
                                                    }, error -> log.error("Failed to cache data: " + error.getMessage()));
                                        } catch (Exception e) {
                                            log.error("Failed to serialize data for caching: " + e.getMessage());
                                        }
                                    }
                                    return Flux.fromIterable(amenities);
                                }));
    }

    public Mono<Amenity> save(Amenity myEntry) {
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

    public Mono<Amenity> saveNew(Amenity myEntry) {
//        user.setPassword(passwordEn.encode(user.getPassword()));
        myEntry.setCreatedOn(Utility.getCurrentTime());
        myEntry.setLastUpdatedOn(Utility.getCurrentTime());
        return repository.save(myEntry)
                .doOnNext(savedEntry -> redisTemplate.opsForValue()
                        .set(KEY_PREFIX + savedEntry.getId(), Utility.serialize(savedEntry))
                        .subscribe());
//        return repository.save(myEntry);
    }

    public Mono<Amenity> getById(ObjectId id) {
        String key = KEY_PREFIX + id.toHexString();

        return redisTemplate.opsForValue().get(key)
                .flatMap(json -> Mono.fromCallable(() -> objectMapper.readValue(json, Amenity.class)))
                .onErrorResume(e -> Mono.empty()) // if deserialization fails, ignore cache and load from DB
                .switchIfEmpty(
                        repository.findById(id).flatMap(entry -> Mono.fromCallable(() -> objectMapper.writeValueAsString(entry))
                                .flatMap(json -> redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(5)).thenReturn(entry)))
                );
//        return repository.findById(id);
    }

    public Flux<Amenity> getByVenueId(String id) {
        String key = KEY_PREFIX + id;

        return redisTemplate.opsForValue()
                .get(key)
                .flatMapMany(a -> Utility.deserializeList(a, Amenity.class))
                .switchIfEmpty(repository.findByVenueId(id)
                        .collectList()
                        .doOnNext(amenities -> {
                                    try {
                                        redisTemplate.opsForValue()
                                                .set(key, objectMapper.writeValueAsString(amenities)).subscribe();
                                    } catch (JsonProcessingException e) {
                                        e.printStackTrace();
                                    }
                                }
                        )
                        .flatMapMany(Flux::fromIterable));
    }

//    public Flux<Amenity> getByVenueId(String id){
//        return repository.findByVenueId(id);
//    }

    public void delete(ObjectId id) {
        String key = KEY_PREFIX + id.toHexString();

        repository.deleteById(id)
                .then(redisTemplate.opsForValue().delete(key))
                .subscribe();

//        repository.deleteById(id);
    }
}
