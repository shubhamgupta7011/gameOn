package com.example.GameOn.service;

import com.example.GameOn.entity.PlansAndOffers;
import com.example.GameOn.entity.Ratings;
import com.example.GameOn.entity.UserDetails.UserProfile;
import com.example.GameOn.entity.Venue;
import com.example.GameOn.filters.QueryBuilder;
import com.example.GameOn.repository.RatingRepository;
import com.example.GameOn.repository.UserRepository;
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

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
public class RatingService {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String KEY_PREFIX = "rating::";
    private static final String ALL_KEY_PREFIX = "all_ratings";
    //    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();

    @Autowired
    RatingRepository repository;

    @Autowired
    UserRepository userRepository;

    private final ReactiveMongoTemplate mongoTemplate;

    public RatingService(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Mono<Ratings> addRatingAndUpdateAverage(Ratings rating) {
        // ✅ Save the rating
        return repository.save(rating)
                .flatMap(savedRating -> {
                    // ✅ Update the average rating of the rated user
                    return userRepository.findByUid(savedRating.getUserId())
                            .flatMap(userProfile -> {
                                return repository.findByUserId(savedRating.getUserId())
                                        .collectList()
                                        .flatMap(ratings -> {
                                            // Calculate average ratings
                                            double avgSecurityRating = ratings.stream()
                                                    .mapToDouble(Ratings::getSecurityRating)
                                                    .average()
                                                    .orElse(0.0);

                                            double avgSkillRating = ratings.stream()
                                                    .mapToDouble(Ratings::getSkillRating)
                                                    .average()
                                                    .orElse(0.0);

                                            // Update the user's profile
                                            userProfile.getUserDetails().setSecurityRating(avgSecurityRating);
                                            userProfile.getUserDetails().setSkillRating(avgSkillRating);

                                            return userRepository.save(userProfile)
                                                    .thenReturn(savedRating);
                                        });
                            });
                });
    }

    public Mono<Ratings> save(Ratings entry){
        return repository.save(entry).flatMap(savedEntity -> {
            String key = KEY_PREFIX + savedEntity.getId();
            return redisTemplate.opsForValue().delete(key)       // Invalidate cache
                    .then(Mono.fromCallable(() -> objectMapper.writeValueAsString(savedEntity))) // Serialize new data
                    .flatMap(json -> redisTemplate.opsForValue().set(key, json))  // Update cache
                    .thenReturn(savedEntity);
        });
    }

    public Flux<Ratings> getFilteredList(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
        String key = Utility.generateCacheKey(ALL_KEY_PREFIX,filters,page,size,sortBy,sortOrder);
        Query query = QueryBuilder.buildQuery(filters, page, size, sortBy, sortOrder);

        return redisTemplate.opsForValue().get(key)
                .flatMapMany(cachedData -> Utility.deserializeCache(cachedData, Ratings[].class))
                .switchIfEmpty(Utility.fetchAndCacheFromDB(query, key, Ratings.class, mongoTemplate,redisTemplate));
    }

    public Mono<Ratings> saveNew(Ratings myEntry){
        myEntry.setCreatedOn(Utility.getCurrentTime());
        myEntry.setLastUpdatedOn(Utility.getCurrentTime());
        return repository.save(myEntry)
                .doOnNext(savedEntry -> redisTemplate.opsForValue()
                        .set(KEY_PREFIX + savedEntry.getId(), Utility.serialize(savedEntry))
                        .subscribe());
//        return repository.save(entry);
    }

    public Flux<Ratings> getAll(){
        return repository.findAll();
    }

    public Mono<Ratings> getById(ObjectId id){
        String key = KEY_PREFIX + id.toHexString();

        return redisTemplate.opsForValue().get(key)
                .flatMap(json -> Mono.fromCallable(() -> objectMapper.readValue(json, Ratings.class)))
                .onErrorResume(e -> Mono.empty()) // if deserialization fails, ignore cache and load from DB
                .switchIfEmpty(
                        repository.findById(id).flatMap(entry -> Mono.fromCallable(() -> objectMapper.writeValueAsString(entry))
                                .flatMap(json -> redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(5)).thenReturn(entry)))
                );
    }

    public Flux<Ratings> getByUserId(String id){
        return repository.findByUserId(id);
    }

    public Flux<Ratings> getByFromUserId(String id){
        return repository.findByFromUserId(id);
    }

    public void delete(ObjectId id){
        String key = KEY_PREFIX + id.toHexString();

        repository.deleteById(id)
                .then(redisTemplate.opsForValue().delete(key))
                .subscribe();

//        repository.deleteById(id);
    }
}
