package com.example.GameOn.service;

import com.example.GameOn.entity.Amenity;
import com.example.GameOn.entity.Booking;
import com.example.GameOn.filters.QueryBuilder;
import com.example.GameOn.repository.BookingRepository;
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
public class BookingService {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String KEY_PREFIX = "booking::";
    private static final String ALL_KEY_PREFIX = "all_bookings";
    //    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();

    @Autowired
    BookingRepository repository;

    private final ReactiveMongoTemplate mongoTemplate;

    public BookingService(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Mono<Booking> save(Booking myEntry){
        return repository.save(myEntry)
                .flatMap(savedEntity -> {
                    String key = KEY_PREFIX + savedEntity.getId();
                    return redisTemplate.opsForValue().delete(key)       // Invalidate cache
                            .then(Mono.fromCallable(() -> objectMapper.writeValueAsString(savedEntity))) // Serialize new data
                            .flatMap(json -> redisTemplate.opsForValue().set(key, json))  // Update cache
                            .thenReturn(savedEntity);
                });
    }

    public Flux<Booking> getFilteredList(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
        String key = Utility.generateCacheKey(ALL_KEY_PREFIX,filters,page,size,sortBy,sortOrder);
        Query query = QueryBuilder.buildQuery(filters, page, size, sortBy, sortOrder);

        return redisTemplate.opsForValue().get(key)
                .flatMapMany(cachedData -> Utility.deserializeCache(cachedData,  Booking[].class))
                .switchIfEmpty(Utility.fetchAndCacheFromDB(query, key, Booking.class, mongoTemplate,redisTemplate));
    }

    public Mono<Booking> saveNew(Booking myEntry){
        myEntry.setCreatedOn(Utility.getCurrentTime());
        myEntry.setLastUpdatedOn(Utility.getCurrentTime());
        return repository.save(myEntry)
                .doOnNext(savedEntry -> redisTemplate.opsForValue()
                        .set(KEY_PREFIX + savedEntry.getId(), Utility.serialize(savedEntry))
                        .subscribe());
//        return repository.save(myEntry);
    }

    public Mono<Booking> getById(ObjectId id){
        String key = KEY_PREFIX + id.toHexString();

        return redisTemplate.opsForValue().get(key)
                .flatMap(json -> Mono.fromCallable(() -> objectMapper.readValue(json, Booking.class)))
                .onErrorResume(e -> Mono.empty()) // if deserialization fails, ignore cache and load from DB
                .switchIfEmpty(
                        repository.findById(id).flatMap(entity -> Mono.fromCallable(() -> objectMapper.writeValueAsString(entity))
                                .flatMap(json -> redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(5)).thenReturn(entity)))
                );
    }

    public Flux<Booking> getByUserId(String uid){
        String key = KEY_PREFIX + uid;

        return redisTemplate.opsForValue().get(key)
                .flatMapMany(a -> Utility.deserializeList(a, Booking.class))
                .switchIfEmpty(repository.findByUserId(uid)
                        .collectList()
                        .doOnNext(amenities -> Utility.cacheItem(key,amenities,redisTemplate))
                        .flatMapMany(Flux::fromIterable));
    }

    public void delete(String id){
        String key = KEY_PREFIX + id;

        repository.deleteById(new ObjectId(id))
                .then(redisTemplate.opsForValue().delete(key)).subscribe();
    }


}
