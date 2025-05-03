package com.example.GameOn.repository;

import com.example.GameOn.entity.Clubs;
import com.example.GameOn.entity.Venue;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Flux;

import java.util.List;

@EnableReactiveMongoRepositories
public interface ClubRepository extends ReactiveMongoRepository<Clubs, ObjectId> {

//    @Query("{ 'skills': { $regex: ?0, $options: 'i' } }")
//    Flux<Venue> findBySkills(String name);

//    @Query("{ ?0: ?1 }")
//    Page<Venue> findByDynamicField(String field, String value, Pageable pageable);

//    @Query("{$and: ?#{[0]}}") // SpEL to dynamically construct query
//    Flux<Venue> findByDynamicFilters(List<Object> filters, Pageable pageable);


//    Venue findByUserName(String username);
}
