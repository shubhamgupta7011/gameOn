package com.example.GameOn.repository;

import com.example.GameOn.entity.TimeSlots;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.List;

@EnableReactiveMongoRepositories
public interface TimeslotRepository extends ReactiveMongoRepository<TimeSlots, ObjectId> {

//    @Query("{$and: ?#{[0]}}") // SpEL to dynamically construct query
//    List<TimeSlots> findByDynamicFilters(List<Object> filters, Pageable pageable);

//    Venue findByUserName(String username);
}
