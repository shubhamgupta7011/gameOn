package com.example.GameOn.service;

import com.example.GameOn.entity.Clubs;
import com.example.GameOn.repository.ClubRepository;
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
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    private static final String CLUB_KEY_PREFIX = "club::";
    private static final String ALL_CLUBS_KEY = "all_clubs";


    private final ReactiveMongoTemplate mongoTemplate;

    private final ReactiveValueOperations<String, Object> valueOps;

    public ClubService(ReactiveMongoTemplate mongoTemplate, ReactiveValueOperations<String, Object> valueOps) {
        this.mongoTemplate = mongoTemplate;
        this.valueOps = valueOps;
    }

//    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();

    public Mono<Clubs> save(Clubs myEntry){
        myEntry.setLastUpdatedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant(ZoneId.of("UTC").getRules().getOffset(Instant.now())).toEpochMilli());
        return repository.save(myEntry);
    }

    public Mono<Clubs> saveNew(Clubs myEntry){
//        user.setPassword(passwordEn.encode(user.getPassword()));
        myEntry.setCreatedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant(ZoneId.of("UTC").getRules().getOffset(Instant.now())).toEpochMilli());
        myEntry.setLastUpdatedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant(ZoneId.of("UTC").getRules().getOffset(Instant.now())).toEpochMilli());
        return repository.save(myEntry);
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

    public Mono<Clubs> getById(ObjectId id){
        String key = CLUB_KEY_PREFIX + id.toHexString();
        return valueOps.get(key)
                .cast(Clubs.class)
                .switchIfEmpty(repository.findById(id)
                        .doOnNext(club -> redisTemplate.opsForValue().set(key, club))
                );

//        return repository.findById(id);
    }

    public void delete(String id){
        repository.deleteById(new ObjectId(id));
    }

}
