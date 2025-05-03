package com.example.GameOn.repository;

import com.example.GameOn.entity.Ratings;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.List;

@EnableReactiveMongoRepositories
public interface RatingRepository extends ReactiveMongoRepository<Ratings, ObjectId> {

//    @Query("{$and: ?#{[0]}}") // SpEL to dynamically construct query
//    List<Ratings> findByDynamicFilters(List<Object> filters, Pageable pageable);

//    Venue findByUserName(String username);
}
