package com.example.GameOn.repository;

import com.example.GameOn.entity.SwipeByUser;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;
import reactor.core.publisher.Flux;

@EnableReactiveCassandraRepositories
public interface SwipeByUserRepository extends ReactiveCassandraRepository<SwipeByUser, SwipeByUser.Key> {
    Flux<SwipeByUser> findByKeySwiperId(String swiperId);
}
