package com.example.GameOn.repository;

import com.example.GameOn.entity.UserDetails.UserProfile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@EnableReactiveMongoRepositories
public interface UserRepository extends ReactiveMongoRepository<UserProfile, ObjectId> {

//    Mono<Users> findByUserName(String username);

    Mono<UserProfile> findByUid(String uid);

    @Query("{'location': { $near: { $geometry: { type: 'Point', coordinates: [?1, ?0] }, $maxDistance: ?2 } } }")
    Flux<UserProfile> findNearby(double lat, double lon, long maxDistanceMeters);

//    Page<Users> findAll(Pageable pageable);
}
