package com.example.GameOn.controller;

import com.example.GameOn.entity.Booking;

import com.example.GameOn.enums.EventStatus;
import com.example.GameOn.enums.EventType;
import com.example.GameOn.enums.Skills;
import com.example.GameOn.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/booking")
@Tag(name = "Booking Controller", description = "Manage Booking or Events")
public class BookingController {

    @Autowired
    BookingService service;

    @Operation(
            summary = "Fetch all Events",
            description = "To fetch Booking/Events and their details on the bases of different filters and we can sort them of different fields"
    )
    @GetMapping("/all")
    public Mono<ResponseEntity<?>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Skills skills,
            @RequestParam(required = false) Boolean availability,
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) String venueId,
            @RequestParam(required = false) String amenityId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) EventType type,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {

        Map<String, Object> filterMap = new HashMap<>();

        if (Objects.nonNull(skills)) filterMap.put("skills", skills);
        if (Objects.nonNull(status)) filterMap.put("status", status);
        if (Objects.nonNull(venueId)) filterMap.put("venueId", venueId);
        if (Objects.nonNull(amenityId)) filterMap.put("amenityId", amenityId);
        if (Objects.nonNull(userId)) filterMap.put("userId", userId);
        if (Objects.nonNull(type)) filterMap.put("type", type);
        if (Objects.nonNull(availability)) filterMap.put("availability", availability);

        return service.getFilteredList(filterMap, page, size, sortBy, sortOrder).collectList() // Convert Flux to Mono<List<Booking>>
                .flatMap(booking -> {
                    if (booking.isEmpty()) {
                        log.info("No booking found.");
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                    } else return Mono.just(ResponseEntity.ok(booking));
                })
                .onErrorResume(e -> {
                    log.error("Error fetching booking: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @Operation(
            summary = "Create Booking",
            description = "To fetch Amenity and their details on the bases of different filters and we can sort them of different fields"
    )
    @PostMapping("/add")
    public Mono<ResponseEntity<Booking>> saveNew(@RequestBody Booking myEntry){

        return service.saveNew(myEntry)
                .map(x->new ResponseEntity<>(x, HttpStatus.CREATED))
                .doOnNext(saved -> log.info("Booking saved successfully: {}", saved))
                .doOnError(error -> log.error("Error Booking booking", error))
                .defaultIfEmpty(ResponseEntity.badRequest().build());

    }

    @Operation(
            summary = "Fetch Booking By Id",
            description = "Fetch Booking By Id"
    )
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Booking>> getById(@PathVariable String id) {
        log.info("Received request to delete Booking: {}", id);
        return service.getById(new ObjectId(id))
                .map(elem -> new ResponseEntity<>(elem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "Delete Booking",
            description = "To Delete Booking"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Update Booking Details",
            description = "To fetch Amenity and their details on the bases of different filters and we can sort them of different fields"
    )
    @PutMapping("/update")
    public Mono<ResponseEntity<Booking>> update(@RequestBody Mono<Booking> myEntryMono) {
        return myEntryMono
                .flatMap(service::save) // Call the service method
                .map(ResponseEntity::ok) // Return 200 OK with saved booking
                .doOnNext(saved -> log.info("Booking saved successfully: {}", saved))
                .doOnError(error -> log.error("Error Booking booking", error))
                .defaultIfEmpty(ResponseEntity.notFound().build()) // If no booking, return 404
                .onErrorResume(e -> {
                    log.error("Error updating booking: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

}
