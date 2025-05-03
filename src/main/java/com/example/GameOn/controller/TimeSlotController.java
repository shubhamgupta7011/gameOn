package com.example.GameOn.controller;

import com.example.GameOn.entity.TimeSlots;
import com.example.GameOn.entity.Venue;
import com.example.GameOn.service.TimeSlotService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@Slf4j
@RequestMapping("/api/time_slot")
@Tag(name = "Time Slot Controller", description = "Manage Time Slot of Venue")
public class TimeSlotController {

//    @Autowired
//    TimeSlotService service;

//    @GetMapping("/all")
//    public Mono<ResponseEntity<?>> getAll(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(required = false) String skills,
//            @RequestParam(required = false) Boolean availability,
//            @RequestParam(defaultValue = "name") String sortBy,
//            @RequestParam(defaultValue = "asc") String sortOrder
//    ) {
//
//        Map<String, Object> filterMap = new HashMap<>();
//
//        if (Objects.nonNull(skills)) {
//            filterMap.put("skills", skills);
//        }
//        if (Objects.nonNull(availability)) {
//            filterMap.put("availability", availability);
//        }
//
//        return service.getFilteredList(filterMap, page, size, sortBy, sortOrder)
//                .collectList() // Convert Flux to Mono<List<Venue>>
//                .flatMap(timeSlots -> {
//                    if (timeSlots.isEmpty()) {
//                        System.out.println("No TimeSlot avaliable.");
//                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
//                    } else {
//                        return Mono.just(ResponseEntity.ok(timeSlots));
//                    }
//                })
//                .onErrorResume(e -> {
//                    System.err.println("Error fetching Timeslot: " + e.getMessage());
//                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
//                });
//    }
//
//    @PostMapping
//    public Mono<ResponseEntity<TimeSlots>> saveNewDocument(@RequestBody Mono<TimeSlots> myEntryMono) {
//        return myEntryMono
//                .flatMap(service::saveNew) // Call service method reactively
//                .doOnNext(saved -> log.info("TimeSlots saved successfully: {}", saved))
//                .doOnError(error -> log.error("Error TimeSlots Venue", error))
//                .map(savedEntry -> ResponseEntity.status(HttpStatus.CREATED).body(savedEntry)) // Return 201 CREATED
//                .onErrorResume(e -> {
//                    System.err.println("Error saving document: " + e.getMessage());
//                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
//                });
//    }
//
//    @GetMapping("{id}")
//    public Mono<ResponseEntity<TimeSlots>> getById(@PathVariable String id) {
//        return service.getById(new ObjectId(id))
//                .map(elem -> new ResponseEntity<>(elem, HttpStatus.OK))
//                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }
//
//    @DeleteMapping("{id}")
//    public ResponseEntity<?> deleteById(@PathVariable String id) {
//        service.delete(new ObjectId(id));
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @PutMapping
//    public Mono<ResponseEntity<TimeSlots>> update(@RequestBody Mono<TimeSlots> myEntryMono) {
//        return myEntryMono
//                .flatMap(service::save) // Call the service method
//                .map(ResponseEntity::ok) // Return 200 OK with saved venue
//                .doOnNext(saved -> log.info("TimeSlots saved successfully: {}", saved))
//                .doOnError(error -> log.error("Error TimeSlots Venue", error))
//                .defaultIfEmpty(ResponseEntity.notFound().build()) // If no venue, return 404
//                .onErrorResume(e -> {
//                    System.err.println("Error updating venue: " + e.getMessage());
//                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
//                });
//    }

}
