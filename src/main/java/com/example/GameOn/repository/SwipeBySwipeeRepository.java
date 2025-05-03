package com.example.GameOn.repository;

import com.example.GameOn.entity.SwipeBySwipee;
import com.example.GameOn.enums.SwipeType;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;
import reactor.core.publisher.Flux;

@EnableReactiveCassandraRepositories
public interface SwipeBySwipeeRepository extends ReactiveCassandraRepository<SwipeBySwipee, SwipeBySwipee.Key> {
    Flux<SwipeBySwipee> findByKeySwipeeId(String swipeeId);


    Flux<SwipeBySwipee> findByKeySwipeeIdAndSwipeType(String swipeeId, SwipeType swipeType);
}
