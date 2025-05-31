package com.example.GameOn.repository;

import com.example.GameOn.entity.PlansAndOffers;
import com.example.GameOn.entity.UserDetails.MatchPreference;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Mono;

@EnableReactiveMongoRepositories
public interface MatchPrefrenceRepository extends ReactiveMongoRepository<MatchPreference, ObjectId> {

    Mono<MatchPreference> findByUserId(String userId);
}
