package com.example.GameOn.controller;

import com.example.GameOn.entity.Ratings;
import com.example.GameOn.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/api/rating")
@Tag(name = "Rating Controller", description = "Manage Rating")
public class RatingController {

    @Autowired
    RatingService service;

    @Operation(
            summary = "Fetch all Rating",
            description = "To fetch Rating and their details on the bases of different filters and we can sort them of different fields"
    )
    @GetMapping("/all")
    public Mono<ResponseEntity<?>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) Boolean availability,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {

        Map<String, Object> filterMap = new HashMap<>();

        if (Objects.nonNull(skills)) filterMap.put("skills", skills);
        if (Objects.nonNull(availability)) filterMap.put("availability", availability);

        return service.getFilteredList(filterMap, page, size, sortBy, sortOrder)
                .collectList() // Convert Flux to Mono<List<Rating>>
                .flatMap(rating -> {
                    if (rating.isEmpty()) {
                        System.out.println("No rating found.");
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                    } else {
                        return Mono.just(ResponseEntity.ok(rating));
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Error fetching rating: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @Operation(
            summary = "Rating to a user",
            description = "To Rate a user on the basis of past experience with that user in Event like security rating and skill rating"
    )
    @PostMapping
    public Mono<ResponseEntity<Ratings>> saveNew(@RequestBody Mono<Ratings> myEntryMono) {
        return myEntryMono
                .flatMap(service::saveNew) // Call service method reactively
                .doOnNext(saved -> log.info("Ratings saved successfully: {}", saved))
                .doOnError(error -> log.error("Error Ratings rating", error))
                .map(savedEntry -> ResponseEntity.status(HttpStatus.CREATED).body(savedEntry)) // Return 201 CREATED
                .onErrorResume(e -> {
                    System.err.println("Error saving document: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                });
    }

    @Operation(
            summary = "Rating to a user",
            description = "To Rate a user on the basis of past experience with that user in Event like security rating and skill rating"
    )
    @PostMapping("/add")
    public Mono<ResponseEntity<Ratings>> saveNewAndUpdateUserRating(@RequestBody Mono<Ratings> myEntryMono) {
        return myEntryMono
                .flatMap(service::addRatingAndUpdateAverage) // Call service method reactively
                .doOnNext(saved -> log.info("Ratings saved successfully: {}", saved))
                .doOnError(error -> log.error("Error Ratings rating", error))
                .map(savedEntry -> ResponseEntity.status(HttpStatus.CREATED).body(savedEntry)) // Return 201 CREATED
                .onErrorResume(e -> {
                    System.err.println("Error saving document: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                });
    }

    @Operation(
            summary = "Fetch particular Ratings",
            description = "To Fetch Particular Ratings of a user"
    )
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Ratings>> getById(@PathVariable String id) {
        return service.getById(new ObjectId(id))
                .map(elem -> new ResponseEntity<>(elem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "Fetch all Ratings to user",
            description = "To Fetch all Ratings for a particular user in user profile"
    )
    @GetMapping("/to_user/{uid}")
    public Flux<ResponseEntity<Ratings>> getByUserId(@PathVariable String uid) {
        return service.getByUserId(uid)
                .map(elem -> new ResponseEntity<>(elem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "Fetch all Ratings by user",
            description = "To Fetch all Ratings by a particular user"
    )
    @GetMapping("/from_user/{uid}")
    public Flux<ResponseEntity<Ratings>> getByFromUserId(@PathVariable String uid) {
        return service.getByFromUserId(uid)
                .map(elem -> new ResponseEntity<>(elem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "Delete Rating and comments",
            description = "To fetch Amenity and their details on the bases of different filters and we can sort them of different fields"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        service.delete(new ObjectId(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Update Ratings",
            description = "To update ratings of the user"
    )
    @PutMapping("/update")
    public Mono<ResponseEntity<Ratings>> update(@RequestBody Mono<Ratings> myEntry) {
        return myEntry
                .flatMap(service::save) // Call the service method
                .map(ResponseEntity::ok) // Return 200 OK with saved rating
                .defaultIfEmpty(ResponseEntity.notFound().build()) // If no rating, return 404
                .doOnNext(saved -> log.info("Ratings saved successfully: {}", saved))
                .doOnError(error -> log.error("Error Ratings rating", error))
                .onErrorResume(e -> {
                    System.err.println("Error updating rating: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

}
