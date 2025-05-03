package com.example.GameOn.service;

import com.example.GameOn.entity.Booking;
import com.example.GameOn.entity.Venue;
import com.example.GameOn.repository.BookingRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class BookingService {

    @Autowired
    BookingRepository repository;

    private final ReactiveMongoTemplate mongoTemplate;

    public BookingService(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

//    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();

    public Mono<Booking> save(Booking myEntry){
        return repository.save(myEntry);
    }

    public Flux<Booking> getFilteredList(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
        Query query = new Query();

        // ✅ Apply dynamic filters
        if (filters != null && !filters.isEmpty()) {
            filters.forEach((key, value) -> query.addCriteria(Criteria.where(key).is(value)));
        }

        // ✅ Sorting
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            query.with(Sort.by(direction, sortBy));
        }

        // ✅ Pagination
        query.skip((long) page * size).limit(size);

        return mongoTemplate.find(query, Booking.class);
    }

    public Mono<Booking> saveNew(Booking myEntry){ return repository.save(myEntry); }

    public Flux<Booking> getAll(){
        return repository.findAll();
    }

    public Mono<Booking> getById(ObjectId id){
        return repository.findById(id);
    }

    public void delete(String id){
        repository.deleteById(new ObjectId(id));
    }


}
