package com.example.GameOn.repository;

import com.example.GameOn.entity.UserDetails.Users;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Mono;

@EnableReactiveMongoRepositories
public interface UserRepository extends ReactiveMongoRepository<Users, ObjectId> {

//    Mono<Users> findByUserName(String username);

    Mono<Users> findByUid(String uid);

//    Page<Users> findAll(Pageable pageable);
}
