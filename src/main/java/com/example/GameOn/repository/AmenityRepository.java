package com.example.GameOn.repository;

import com.example.GameOn.entity.Amenity;
import com.example.GameOn.entity.Venue;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Flux;

import java.util.List;

@EnableReactiveMongoRepositories
public interface AmenityRepository extends ReactiveMongoRepository<Amenity, ObjectId> {

//    @Query("{$and: ?#{[0]}}") // SpEL to dynamically construct query
//    List<Amenity> findByDynamicFilters(List<Object> filters, Pageable pageable);

    Flux<Amenity> findByVenueId(String venueId);
}
