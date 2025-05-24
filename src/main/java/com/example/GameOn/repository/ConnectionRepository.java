package com.example.GameOn.repository;

import com.example.GameOn.entity.Amenity;
import com.example.GameOn.entity.Booking;
import com.example.GameOn.entity.Connection;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@EnableReactiveMongoRepositories
public interface ConnectionRepository extends ReactiveMongoRepository<Connection, ObjectId> {

//    @Query("{$and: ?#{[0]}}") // SpEL to dynamically construct query
//    List<Booking> findByDynamicFilters(List<Object> filters, Pageable pageable);

    Mono<Connection> findByUsersId(String venueId);
}
