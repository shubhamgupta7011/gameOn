package com.example.GameOn.service;

import com.example.GameOn.entity.Connection;
import com.example.GameOn.entity.ConnectionRequest;
import com.example.GameOn.entity.Ratings;
import com.example.GameOn.enums.RequestStatus;
import com.example.GameOn.filters.QueryBuilder;
import com.example.GameOn.repository.ConnectionRepository;
//import com.example.GameOn.repository.ConnectionRequestRepository;
import com.example.GameOn.repository.ConnectionRequestRepository;
import com.example.GameOn.utils.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;


@Service
public class ConnectionService {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String KEY_PREFIX = "Connection::";
    private static final String ALL_KEY_PREFIX = "all_Connection";
    //    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();

    @Autowired
    ConnectionRepository repository;

    @Autowired
    ConnectionRequestRepository connectionRequestRepository;

    private final ReactiveMongoTemplate mongoTemplate;

    public ConnectionService(ReactiveMongoTemplate mongoTemplate, ReactiveValueOperations<String, String> valueOps) {
        this.mongoTemplate = mongoTemplate;
    }

    public Mono<ConnectionRequest> save(ConnectionRequest myEntry){
        myEntry.setRespondedAt(Utility.getCurrentTime());

        return connectionRequestRepository.save(myEntry).map(v-> {
            if (myEntry.getStatus().equals(RequestStatus.ACCEPTED)){
                repository.findByUsersId(v.getToUserId()).map(x -> {
                    List<String> connections = x.getFUserId();
                    connections.add(v.getFromUserId());
                    repository.save(x);
                    return null;
                });
        }
            return v;
        }).map(v->{
            if (myEntry.getStatus().equals(RequestStatus.ACCEPTED)) {
                repository.findByUsersId(v.getFromUserId()).map(x -> {
                    List<String> connections = x.getFUserId();
                    connections.add(v.getToUserId());
                    repository.save(x);
                    return null;
                });
            }
            return v;
        });
    }

    public Mono<ConnectionRequest> alreadyRequest(String toUserId, String fromUserId){
        return connectionRequestRepository.findByToUserIdAndFromUserId(toUserId,fromUserId);
    }

    public Flux<ConnectionRequest> pendingRequestList(String fromUserId){
        return connectionRequestRepository.findByFromUserId(fromUserId);
    }

    public Flux<ConnectionRequest> sendRequestList(String fromUserId){
        return connectionRequestRepository.findByToUserId(fromUserId);
    }

    public Mono<ConnectionRequest> saveNew(ConnectionRequest myEntry){
        myEntry.setCreatedAt(Utility.getCurrentTime());
        myEntry.setRespondedAt(null);
        myEntry.setStatus(RequestStatus.PENDING);
        repository.findByUsersId(myEntry.getFromUserId());
        return connectionRequestRepository.save(myEntry);
    }

    public Mono<Connection> getByUserId(String uid) {
        String key = KEY_PREFIX + uid;

        return redisTemplate.opsForValue().get(key)
                .flatMap(json -> Mono.fromCallable(() -> objectMapper.readValue(json, Connection.class)))
                .onErrorResume(e -> Mono.empty()) // if deserialization fails, ignore cache and load from DB
                .switchIfEmpty(
                        repository.findByUsersId(uid).flatMap(entry -> Mono.fromCallable(() -> objectMapper.writeValueAsString(entry))
                                .flatMap(json -> redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(15)).thenReturn(entry)))
                );
    }

    public void delete(String id){
        String key = KEY_PREFIX + id;

        repository.deleteById(new ObjectId(id))
                .then(redisTemplate.opsForValue().delete(key))
                .subscribe();
    }


}
