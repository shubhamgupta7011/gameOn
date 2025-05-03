package com.example.GameOn.repository;

import com.example.GameOn.entity.MatchKey;
import com.example.GameOn.entity.Matches;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;
import reactor.core.publisher.Flux;

@EnableReactiveCassandraRepositories
public interface MatchesByUserRepository extends ReactiveCassandraRepository<Matches, MatchKey> {
    Flux<Matches> findByKeyUserId(String userId);
}
