package com.example.GameOn.repository;

import com.example.GameOn.entity.Feedback;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Flux;

import java.util.List;

@EnableReactiveMongoRepositories
public interface FeedBacksRepository extends ReactiveMongoRepository<Feedback, ObjectId> {

//    Venue findByUserName(String username);
    Flux<Feedback> findByVenueId(String venueId);

//    @Query("{$and: ?#{[0]}}") // SpEL to dynamically construct query
//    List<Feedback> findByDynamicFilters(List<Object> filters, Pageable pageable);
}
