package com.example.GameOn.service;

import com.example.GameOn.entity.UserDetails.UserProfile;
import com.example.GameOn.entity.Venue;
import com.example.GameOn.repository.VenueRepository;
import com.example.GameOn.utils.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class VenueService {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private final ReactiveValueOperations<String, String> valueOps;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String KEY_PREFIX = "venue::";
    private static final String ALL_KEY_PREFIX = "all_venue";
    //    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();

    @Autowired
    VenueRepository repository;

    private final ReactiveMongoTemplate mongoTemplate;

    public VenueService(ReactiveMongoTemplate mongoTemplate, ReactiveValueOperations<String, String> valueOps) {
        this.mongoTemplate = mongoTemplate;
        this.valueOps = valueOps;
    }

    public Mono<Venue> save(Venue myEntry){
        myEntry.setLastUpdatedOn(Utility.getCurrentTime());
        return repository.save(myEntry).flatMap(savedEntity -> {
            String key = KEY_PREFIX + savedEntity.getId();
            return redisTemplate.opsForValue().delete(key)       // Invalidate cache
                    .then(Mono.fromCallable(() -> objectMapper.writeValueAsString(savedEntity))) // Serialize new data
                    .flatMap(json -> redisTemplate.opsForValue().set(key, json))  // Update cache
                    .thenReturn(savedEntity);
        });
    }

    public Mono<Venue> saveNew(Venue myEntry){
        myEntry.setCreatedOn(Utility.getCurrentTime());
        myEntry.setLastUpdatedOn(Utility.getCurrentTime());

        return repository.save(myEntry)
                .doOnNext(savedEntry -> redisTemplate.opsForValue()
                        .set(KEY_PREFIX + savedEntry.getId(), Utility.serialize(savedEntry))
                        .subscribe());
    }

    public Flux<Venue> getFilteredList(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
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

        return mongoTemplate.find(query, Venue.class);
    }

    public Mono<Venue> getById(ObjectId id){
        String key = KEY_PREFIX + id.toHexString();

        return redisTemplate.opsForValue().get(key)
                .flatMap(json -> Mono.fromCallable(() -> objectMapper.readValue(json, Venue.class)))
                .onErrorResume(e -> Mono.empty()) // if deserialization fails, ignore cache and load from DB
                .switchIfEmpty(
                        repository.findById(id).flatMap(entry -> Mono.fromCallable(() -> objectMapper.writeValueAsString(entry))
                                .flatMap(json -> redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(5)).thenReturn(entry)))
                );

//        return repository.findById(id);
    }

    public void delete(String id){
        String key = KEY_PREFIX + id;

        repository.deleteById(new ObjectId(id))
                .then(redisTemplate.opsForValue().delete(key))
                .subscribe();
//        repository.deleteById(new ObjectId(id));
    }

}
