package com.example.GameOn.service;

import com.example.GameOn.entity.UserDetails.UserProfile;
import com.example.GameOn.repository.UserRepository;
import com.example.GameOn.filters.QueryBuilder;
import com.example.GameOn.utils.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.*;
import java.util.Map;

@Component
public class UserService {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private final ReactiveValueOperations<String, String> valueOps;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String KEY_PREFIX = "user::";
    private static final String ALL_KEY = "all_users";

    @Autowired
    UserRepository userRepository;

    private final ReactiveMongoTemplate mongoTemplate;

    public UserService(ReactiveMongoTemplate mongoTemplate, ReactiveValueOperations<String, String> valueOps) {
        this.mongoTemplate = mongoTemplate;
        this.valueOps = valueOps;
    }

    public Flux<UserProfile> getFilteredList(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
        String key = Utility.generateCacheKey(KEY_PREFIX,filters,page,size,sortBy,sortOrder);
        Query query = QueryBuilder.buildQuery(filters,page,size,sortBy,sortOrder);
        return mongoTemplate.find(query, UserProfile.class);
    }

    public Mono<UserProfile> saveUser(UserProfile user){
        user.setLastUpdatedOn(Utility.getCurrentTime());
        return userRepository.save(user)
                .flatMap(savedEntity -> {
            String key = KEY_PREFIX + savedEntity.getId();
            return redisTemplate.opsForValue().delete(key)       // Invalidate cache
                    .then(Mono.fromCallable(() -> objectMapper.writeValueAsString(savedEntity))) // Serialize new data
                    .flatMap(json -> redisTemplate.opsForValue().set(key, json))  // Update cache
                    .thenReturn(savedEntity);
        });
    }

    public Mono<UserProfile> findByUserId(String userId){
        return userRepository.findByUid(userId);
    }

    public Mono<UserProfile> saveNewUser(UserProfile myEntry){
        myEntry.setCreatedOn(Utility.getCurrentTime());
        myEntry.setLastUpdatedOn(Utility.getCurrentTime());

        return userRepository.save(myEntry)
                .doOnNext(savedEntry -> redisTemplate.opsForValue()
                        .set(KEY_PREFIX + savedEntry.getId(), Utility.serialize(savedEntry))
                        .subscribe());
    }

    public Mono<UserProfile> getById(ObjectId id){
        String key = KEY_PREFIX + id.toHexString();

        return redisTemplate.opsForValue().get(key)
                .flatMap(json -> Mono.fromCallable(() -> objectMapper.readValue(json, UserProfile.class)))
                .onErrorResume(e -> Mono.empty()) // if deserialization fails, ignore cache and load from DB
                .switchIfEmpty(
                        userRepository.findById(id).flatMap(entry -> Mono.fromCallable(() -> objectMapper.writeValueAsString(entry))
                                .flatMap(json -> redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(5)).thenReturn(entry)))
                );
//        return userRepository.findById(id);
    }

    public void delete(ObjectId id){
        String key = KEY_PREFIX + id.toHexString();

        userRepository.deleteById(id)
                .then(redisTemplate.opsForValue().delete(key))
                .subscribe();
//        userRepository.deleteById(id);
    }

    public String generateHashFromPhone(String phone) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(phone.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash).substring(0, 16); // Trim to 16 chars
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating user ID", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public Mono<UserProfile> updateUserLocation(String userId, double lat, double lon) {
        return userRepository.findByUid(userId)
                .flatMap(user -> {
                    user.getLocation().setLongitude(lon);
                    user.getLocation().setLatitude(lat);
                    return userRepository.save(user);
                });
    }
}
