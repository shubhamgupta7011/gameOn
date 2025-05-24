package com.example.GameOn.service;

import com.example.GameOn.entity.Amenity;
import com.example.GameOn.repository.AmenityRepository;
import com.example.GameOn.filters.QueryBuilder;
import com.example.GameOn.utils.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AmenityService {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String KEY_PREFIX = "amenity::";
    private static final String ALL_KEY_PREFIX = "all_amenities";
    //    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();

    @Autowired
    AmenityRepository repository;

    private final ReactiveMongoTemplate mongoTemplate;

    public AmenityService(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Flux<Amenity> getFilteredList(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
        String key = Utility.generateCacheKey(ALL_KEY_PREFIX,filters, page, size, sortBy, sortOrder);
        Query query = QueryBuilder.buildQuery(filters, page, size, sortBy, sortOrder);

        return redisTemplate.opsForValue().get(key)
                .flatMapMany(cachedData -> Utility.deserializeCache(cachedData,Amenity[].class))
                .switchIfEmpty(Utility.fetchAndCacheFromDB(query, key, Amenity.class, mongoTemplate,redisTemplate));
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
        myEntry.setCreatedOn(Utility.getCurrentTime());
        myEntry.setLastUpdatedOn(Utility.getCurrentTime());
        return repository.save(myEntry)
                .doOnNext(savedEntry ->
                        Utility.cacheItem(KEY_PREFIX + savedEntry.getId(),savedEntry,redisTemplate)
                );
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
    }

    public Flux<Amenity> getByVenueId(String id) {
        String key = KEY_PREFIX + id;

        return redisTemplate.opsForValue().get(key)
                .flatMapMany(a -> Utility.deserializeList(a, Amenity.class))
                .switchIfEmpty(repository.findByVenueId(id)
                        .collectList()
                        .doOnNext(amenities -> Utility.cacheItem(key,amenities,redisTemplate))
                        .flatMapMany(Flux::fromIterable));
    }

    public void delete(ObjectId id) {
        String key = KEY_PREFIX + id.toHexString();

        repository.deleteById(id)
                .then(redisTemplate.opsForValue().delete(key))
                .subscribe();

//        repository.deleteById(id);
    }
}
