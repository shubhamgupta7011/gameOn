package com.example.GameOn.service;

import com.example.GameOn.entity.Connection;
import com.example.GameOn.entity.ConnectionRequest;
import com.example.GameOn.enums.RequestStatus;
import com.example.GameOn.filters.QueryBuilder;
import com.example.GameOn.repository.ConnectionRepository;
import com.example.GameOn.repository.ConnectionRequestRepository;
import com.example.GameOn.utils.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class ConnectionService {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

//    private final ObjectMapper objectMapper = new ObjectMapper();
//    private static final String KEY_PREFIX = "Connection::";
//    private static final String ALL_KEY_PREFIX = "all_Connection";
//    //    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();
//
//    @Autowired
//    ConnectionRepository repository;
//
//    @Autowired
//    ConnectionRequestRepository connectionRequestRepository;
//
//    private final ReactiveMongoTemplate mongoTemplate;
//
//    public ConnectionService(ReactiveMongoTemplate mongoTemplate, ReactiveValueOperations<String, String> valueOps) {
//        this.mongoTemplate = mongoTemplate;
//    }
//
//    public Mono<ConnectionRequest> save(ConnectionRequest myEntry){
//        myEntry.setRespondedAt(Utility.getCurrentTime());
//
//        return connectionRequestRepository.save(myEntry);
//    }
//
//    public Mono<ConnectionRequest> saveNew(ConnectionRequest myEntry){
//        myEntry.setCreatedAt(Utility.getCurrentTime());
//        myEntry.setRespondedAt(null);
//        myEntry.setStatus(RequestStatus.PENDING);
//        repository.findByUsersId(myEntry.getFromUserId()).;
//        return connectionRequestRepository.save(myEntry);
//    }

//    public Mono<Connection> getByUserId(String uid) {
//        String key = KEY_PREFIX + uid;
//
//        return redisTemplate.opsForValue().get(key)
//                .flatMapMany(json -> Utility.deserializeToFlux(json, Connection[].class))
//                .switchIfEmpty(
//                        repository.findByUsersId(uid).collectList()
//                                .flatMap(connections ->
//                                        Utility.serializeMono(connections)
//                                                .flatMap(json ->
//                                                        redisTemplate.opsForValue().set(
//                                                                key, json, Duration.ofMinutes(5)
//                                                        ).thenReturn(connections)
//                                                )
//                                )
//                                .flatMapMany(Flux::fromIterable)
//                );
//    }

//    public void delete(String id){
//        String key = KEY_PREFIX + id;
//
//        repository.deleteById(new ObjectId(id))
//                .then(redisTemplate.opsForValue().delete(key))
//                .subscribe();
//    }


}
