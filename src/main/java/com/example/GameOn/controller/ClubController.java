package com.example.GameOn.controller;

import com.example.GameOn.entity.Clubs;

import com.example.GameOn.service.ClubService;

import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/club")
@Tag(name = "Club Controller", description = "Manage Club")
public class ClubController {

    @Autowired
    ClubService service;

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
                .collectList() // Convert Flux to Mono<List<Clubs>>
                .flatMap(Clubs -> {
                    if (Clubs.isEmpty()) {
                        System.out.println("No Clubs found.");
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                    } else {
                        return Mono.just(ResponseEntity.ok(Clubs));
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Error fetching Clubs: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @Operation(
            summary = "Fetch all Amenity",
            description = "To fetch Amenity and their details on the bases of different filters and we can sort them of different fields"
    )
    @PostMapping
    public Mono<ResponseEntity<Clubs>> saveNew(@RequestBody Clubs myEntry){

            return service.saveNew(myEntry)
                    .doOnNext(saved -> log.info("Clubs saved successfully: {}", saved))
                    .doOnError(error -> log.error("Error saving Clubs", error))
                    .map(x->new ResponseEntity<>(x, HttpStatus.CREATED))
                    .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    }

    @Operation(
            summary = "Fetch all Amenity",
            description = "To fetch Amenity and their details on the bases of different filters and we can sort them of different fields"
    )
    @GetMapping("{id}")
    public Mono<ResponseEntity<Clubs>> getById(@PathVariable String id) {
        return service.getById(new ObjectId(id))
                .map(elem -> new ResponseEntity<>(elem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "Fetch all Amenity",
            description = "To fetch Amenity and their details on the bases of different filters and we can sort them of different fields"
    )
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Fetch all Amenity",
            description = "To fetch Amenity and their details on the bases of different filters and we can sort them of different fields"
    )
    @PutMapping
    public Mono<ResponseEntity<Clubs>> update(@RequestBody Mono<Clubs> myEntryMono) {
        return myEntryMono
                .flatMap(service::save) // Call the service method
                .map(ResponseEntity::ok) // Return 200 OK with saved Clubs
                .doOnNext(saved -> log.info("Clubs saved successfully: {}", saved))
                .doOnError(error -> log.error("Error saving Clubs", error))
                .defaultIfEmpty(ResponseEntity.notFound().build()) // If no Clubs, return 404
                .onErrorResume(e -> {
                    System.err.println("Error updating Clubs: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

}
