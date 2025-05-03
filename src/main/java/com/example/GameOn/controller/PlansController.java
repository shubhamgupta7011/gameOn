package com.example.GameOn.controller;

import com.example.GameOn.entity.PlansAndOffers;
import com.example.GameOn.service.PlansService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/plans_offers")
@Tag(name = "PlansAndOffers Controller", description = "Manage PlansAndOffers")
public class PlansController {

    @Autowired
    PlansService service;


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
                .collectList() // Convert Flux to Mono<List<PlansAndOffers>>
                .flatMap(PlansAndOffers -> {
                    if (PlansAndOffers.isEmpty()) {
                        System.out.println("No PlansAndOffers found.");
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                    } else {
                        return Mono.just(ResponseEntity.ok(PlansAndOffers));
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Error fetching PlansAndOffers: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping
    public Mono<ResponseEntity<PlansAndOffers>> saveNew(@RequestBody PlansAndOffers myEntry){

            return service.saveNew(myEntry)
                    .doOnNext(saved -> log.info("PlansAndOffers saved successfully: {}", saved))
                    .doOnError(error -> log.error("Error saving PlansAndOffers", error))
                    .map(x->new ResponseEntity<>(x, HttpStatus.CREATED))
                    .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<PlansAndOffers>> getById(@PathVariable String id) {
        return service.getById(new ObjectId(id))
                .map(elem -> new ResponseEntity<>(elem, HttpStatus.OK))
                .doOnError(error -> log.error("Not getting any PlansAndOffers", error))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public Mono<ResponseEntity<PlansAndOffers>> update(@RequestBody Mono<PlansAndOffers> myEntryMono) {
        return myEntryMono
                .flatMap(service::save) // Call the service method
                .map(ResponseEntity::ok) // Return 200 OK with saved PlansAndOffers
                .doOnNext(saved -> log.info("PlansAndOffers saved successfully: {}", saved))
                .doOnError(error -> log.error("Error saving PlansAndOffers", error))
                .defaultIfEmpty(ResponseEntity.notFound().build()) // If no PlansAndOffers, return 404
                .onErrorResume(e -> {
                    System.err.println("Error updating PlansAndOffers: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

}
