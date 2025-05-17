package com.example.GameOn.repository;

import com.example.GameOn.entity.Ratings;
import com.example.GameOn.entity.UserDetails.UserProfile;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@EnableReactiveMongoRepositories
public interface RatingRepository extends ReactiveMongoRepository<Ratings, ObjectId> {

//    @Query("{$and: ?#{[0]}}") // SpEL to dynamically construct query
//    List<Ratings> findByDynamicFilters(List<Object> filters, Pageable pageable);

//    Venue findByUserName(String username);

    Flux<Ratings> findByUserId(String userId);
    Flux<Ratings> findByFromUserId(String fromUserId);
}
