package com.example.GameOn.controller;

import com.example.GameOn.entity.Feedback;
//import com.example.GameOn.service.FeedBacksService;
import com.example.GameOn.service.FeedBacksService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/api/feedback")
public class FeedBackController {

    @Autowired
    FeedBacksService service;

    @Operation(
            summary = "Fetch all Amenity",
            description = "To fetch Amenity and their details on the bases of different filters and we can sort them of different fields"
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

        if (Objects.nonNull(skills)) {
            filterMap.put("skills", skills);
        }
        if (Objects.nonNull(availability)) {
            filterMap.put("availability", availability);
        }

        return service.getFilteredList(filterMap, page, size, sortBy, sortOrder)
                .collectList() // Convert Flux to Mono<List<Feedback>>
                .flatMap(feedback -> {
                    if (feedback.isEmpty()) {
                        System.out.println("No FeedBack found.");
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                    } else {
                        return Mono.just(ResponseEntity.ok(feedback));
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Error fetching FeedBack: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @Operation(
            summary = "Fetch all Amenity",
            description = "To fetch Amenity and their details on the bases of different filters and we can sort them of different fields"
    )
    @PostMapping("/add")
    public Mono<ResponseEntity<Feedback>> saveNew(@RequestBody Feedback myEntry){

        return service.saveNewFeedback(myEntry)
                .doOnNext(saved -> log.info("Feedback saved successfully: {}", saved))
                .doOnError(error -> log.error("Error in Feedback save", error))
                .map(x->new ResponseEntity<>(x, HttpStatus.CREATED))
                .defaultIfEmpty(ResponseEntity.badRequest().build());

    }

    @Operation(
            summary = "Fetch all Amenity",
            description = "To fetch Amenity and their details on the bases of different filters and we can sort them of different fields"
    )
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Feedback>> getById(@PathVariable String id) {
        Mono<Feedback> re = service.getById(new ObjectId(id));
        return re.map(elem -> new ResponseEntity<>(elem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "Fetch all Amenity",
            description = "To fetch Amenity and their details on the bases of different filters and we can sort them of different fields"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Fetch all Amenity",
            description = "To fetch Amenity and their details on the bases of different filters and we can sort them of different fields"
    )
    @PutMapping("/update")
    public Mono<ResponseEntity<Feedback>> update(@RequestBody Mono<Feedback> myEntryMono) {
        return myEntryMono
                .flatMap(service::saveFeedback) // Call the service method
                .map(ResponseEntity::ok) // Return 200 OK with saved Feedback
                .defaultIfEmpty(ResponseEntity.notFound().build()) // If no Feedback, return 404
                .doOnNext(saved -> log.info("Feedback saved successfully: {}", saved))
                .doOnError(error -> log.error("Error Feedback update", error))
                .onErrorResume(e -> {
                    System.err.println("Error updating Feedback: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
}
