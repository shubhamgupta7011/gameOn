package com.example.GameOn.service;

import com.example.GameOn.entity.Ratings;
import com.example.GameOn.entity.Venue;
import com.example.GameOn.repository.RatingRepository;
import com.example.GameOn.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class RatingService {

    @Autowired
    RatingRepository repository;

    @Autowired
    UserRepository userRepository;

    private final ReactiveMongoTemplate mongoTemplate;

    public RatingService(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Mono<Ratings> addRatingAndUpdateAverage(Ratings rating) {
        // ✅ Save the rating
        return repository.save(rating)
                .flatMap(savedRating -> {
                    // ✅ Update the average rating of the rated user
                    return userRepository.findByUid(savedRating.getUserId())
                            .flatMap(userProfile -> {
                                return repository.findByUserId(savedRating.getUserId())
                                        .collectList()
                                        .flatMap(ratings -> {
                                            // Calculate average ratings
                                            double avgSecurityRating = ratings.stream()
                                                    .mapToDouble(Ratings::getSecurityRating)
                                                    .average()
                                                    .orElse(0.0);

                                            double avgSkillRating = ratings.stream()
                                                    .mapToDouble(Ratings::getSkillRating)
                                                    .average()
                                                    .orElse(0.0);

                                            // Update the user's profile
                                            userProfile.getUserDetails().setSecurityRating(avgSecurityRating);
                                            userProfile.getUserDetails().setSkillRating(avgSkillRating);

                                            return userRepository.save(userProfile)
                                                    .thenReturn(savedRating);
                                        });
                            });
                });
    }

//    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();

    public Mono<Ratings> save(Ratings entry){
        return repository.save(entry);
    }

    public Flux<Ratings> getFilteredList(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
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

        return mongoTemplate.find(query, Ratings.class);
    }

    public Mono<Ratings> saveNew(Ratings entry){ return repository.save(entry);}

    public Flux<Ratings> getAll(){
        return repository.findAll();
    }

    public Mono<Ratings> getById(ObjectId id){
        return repository.findById(id);
    }

    public Flux<Ratings> getByUserId(String id){
        return repository.findByUserId(id);
    }

    public Flux<Ratings> getByFromUserId(String id){
        return repository.findByFromUserId(id);
    }

    public void delete(ObjectId id){
        repository.deleteById(id);
    }
}
