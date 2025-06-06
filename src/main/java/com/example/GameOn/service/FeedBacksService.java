package com.example.GameOn.service;

import com.example.GameOn.entity.Feedback;
import com.example.GameOn.filters.QueryBuilder;
import com.example.GameOn.repository.FeedBacksRepository;
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
public class FeedBacksService {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String KEY_PREFIX = "feedback::";
    private static final String ALL_KEY_PREFIX = "all_feedbacks";
    //    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();

    @Autowired
    FeedBacksRepository repository;

    @Autowired
    VenueService venueService;

    private final ReactiveMongoTemplate mongoTemplate;

    public FeedBacksService(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Mono<Feedback> save(Feedback feedback){
        feedback.setLastUpdatedOn(Utility.getCurrentTime());
        return repository.save(feedback)
                .flatMap(savedEntity -> {
                    String key = KEY_PREFIX + savedEntity.getId();
                    return redisTemplate.opsForValue().delete(key)       // Invalidate cache
                            .then(Mono.fromCallable(() -> objectMapper.writeValueAsString(savedEntity))) // Serialize new data
                            .flatMap(json -> redisTemplate.opsForValue().set(key, json))  // Update cache
                            .thenReturn(savedEntity);
                });
    }

    public Flux<Feedback> getFilteredList(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
        String key = Utility.generateCacheKey(ALL_KEY_PREFIX,filters,page,size,sortBy,sortOrder);
        Query query = QueryBuilder.buildQuery(filters, page, size, sortBy, sortOrder);

        return redisTemplate.opsForValue().get(key)
                .flatMapMany(cachedData -> Utility.deserializeCache(cachedData, Feedback[].class))
                .switchIfEmpty(Utility.fetchAndCacheFromDB(query, key, Feedback.class, mongoTemplate,redisTemplate));
    }

    public Mono<Feedback> saveNew(Feedback myEntry){
        myEntry.setCreatedOn(Utility.getCurrentTime());
        myEntry.setLastUpdatedOn(Utility.getCurrentTime());
        return repository.save(myEntry)
                .doOnNext(savedEntry -> redisTemplate.opsForValue()
                        .set(KEY_PREFIX + savedEntry.getId(), Utility.serialize(savedEntry))
                        .subscribe());
    }

    public Mono<Feedback> addRatingAndUpdateAverage(Feedback feedback) {
        // ✅ Save the rating
        return saveNew(feedback)
                .flatMap(savedRating -> {
                    // ✅ Update the average rating of the Venue
                    return venueService.getById(new ObjectId(savedRating.getVenueId()))
                            .flatMap(venue -> {
                                return repository.findByVenueId(savedRating.getVenueId()).collectList()
                                        .flatMap(ratings -> {
                                            venue.setRating(ratings.stream()
                                                    .mapToDouble(Feedback::getRating)
                                                    .average().orElse(0.0));

                                            return venueService.save(venue).thenReturn(savedRating);
                                        });
                            });
                });
    }

    public Flux<Feedback> getByVenueId(String id) {
        return repository.findByVenueId(id);
    }

    public Mono<Feedback> getById(ObjectId id){
        String key = KEY_PREFIX + id.toHexString();

        return redisTemplate.opsForValue().get(key)
                .flatMap(json -> Mono.fromCallable(() -> objectMapper.readValue(json, Feedback.class)))
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
