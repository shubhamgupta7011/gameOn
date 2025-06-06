package com.example.GameOn.repository;

import com.example.GameOn.entity.ConnectionRequest;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@EnableReactiveMongoRepositories
public interface ConnectionRequestRepository extends ReactiveMongoRepository<ConnectionRequest, ObjectId> {

//    @Query("{$and: ?#{[0]}}") // SpEL to dynamically construct query
//    List<Booking> findByDynamicFilters(List<Object> filters, Pageable pageable);

    Mono<ConnectionRequest> findByToUserIdAndFromUserId(String toUserId, String fromUserId);
    Flux<ConnectionRequest> findByToUserId(String toUserId);
    Flux<ConnectionRequest> findByFromUserId( String fromUserId);
}
