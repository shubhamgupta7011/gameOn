package com.example.GameOn.service;

import com.example.GameOn.entity.PlansAndOffers;
import com.example.GameOn.entity.Venue;
import com.example.GameOn.repository.PlansRepository;
import com.example.GameOn.repository.VenueRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Component
public class PlansService {

    @Autowired
    PlansRepository repository;


    private final ReactiveMongoTemplate mongoTemplate;

    public PlansService(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

//    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();

    public Mono<PlansAndOffers> save(PlansAndOffers myEntry){
        myEntry.setLastUpdatedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant(ZoneId.of("UTC").getRules().getOffset(Instant.now())).toEpochMilli());
        return repository.save(myEntry);
    }

    public Mono<PlansAndOffers> saveNew(PlansAndOffers myEntry){
//        user.setPassword(passwordEn.encode(user.getPassword()));
        myEntry.setCreatedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant(ZoneId.of("UTC").getRules().getOffset(Instant.now())).toEpochMilli());
        myEntry.setLastUpdatedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant(ZoneId.of("UTC").getRules().getOffset(Instant.now())).toEpochMilli());
        return repository.save(myEntry);
    }

    public Flux<PlansAndOffers> getFilteredList(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
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

        return mongoTemplate.find(query, PlansAndOffers.class);
    }

    public Mono<PlansAndOffers> getById(ObjectId id){
        return repository.findById(id);
    }

    public void delete(String id){
        repository.deleteById(new ObjectId(id));
    }

}
