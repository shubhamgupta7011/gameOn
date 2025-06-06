package com.example.GameOn.repository;

import com.example.GameOn.entity.Booking;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Flux;

import java.util.List;

@EnableReactiveMongoRepositories
public interface BookingRepository extends ReactiveMongoRepository<Booking, ObjectId> {

Flux<Booking> findByUserId(String id);
}
