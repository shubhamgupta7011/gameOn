package com.example.GameOn.service;

import com.example.GameOn.entity.Feedback;
import com.example.GameOn.entity.Venue;
import com.example.GameOn.repository.FeedBacksRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FeedBacksService {

    @Autowired
    FeedBacksRepository feedBacksRepository;

    private final ReactiveMongoTemplate mongoTemplate;

    public FeedBacksService(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


//    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();

    public Mono<Feedback> saveFeedback(Feedback feedback){
        feedback.setLastUpdatedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant(ZoneId.of("UTC").getRules().getOffset(Instant.now())).toEpochMilli());
        return feedBacksRepository.save(feedback);
    }

    public Flux<Feedback> getFilteredList(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
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

        return mongoTemplate.find(query, Feedback.class);
    }

//    public Page<Feedback> getAllFeedback(Pageable pageable) {
//        return feedBacksRepository.findAll(pageable);
//    }


    public Mono<Feedback> saveNewFeedback(Feedback feedback){
//        user.setPassword(passwordEn.encode(user.getPassword()));
        feedback.setCreatedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant(ZoneId.of("UTC").getRules().getOffset(Instant.now())).toEpochMilli());
        feedback.setLastUpdatedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant(ZoneId.of("UTC").getRules().getOffset(Instant.now())).toEpochMilli());
        return feedBacksRepository.save(feedback);
    }

    public Flux<Feedback> getByVenueId(String id) {
        return feedBacksRepository.findByVenueId(id);
    }

    public Flux<Feedback> getAll(){
        return feedBacksRepository.findAll();
    }

    public Mono<Feedback> getById(ObjectId id){
        return feedBacksRepository.findById(id);
    }

    public void delete(String id){
        feedBacksRepository.deleteById(new ObjectId(id));
    }

}
