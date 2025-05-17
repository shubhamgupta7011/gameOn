package com.example.GameOn.controller;

import com.example.GameOn.entity.Venue;
import com.example.GameOn.responses.VenueRes;
import com.example.GameOn.service.AmenityService;
import com.example.GameOn.service.VenueService;
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
@RequestMapping("/api/venue")
@Tag(name = "Venue Controller", description = "Manage venues")
public class VenueController {

    @Autowired
    VenueService service;

    @Autowired
    AmenityService amenityService;


    @Operation(
            summary = "Fetch all Venue",
            description = "To fetch Venue and their details on the bases of different filters and we can sort them of different fields"
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
                .collectList() // Convert Flux to Mono<List<Venue>>
                .flatMap(venues -> {
                    if (venues.isEmpty()) {
                        System.out.println("No venues found.");
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                    } else {
                        return Mono.just(ResponseEntity.ok(venues));
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Error fetching venues: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @Operation(
            summary = "Create New Venue",
            description = "To Create New Venue for Booking"
    )
    @PostMapping("/add")
    public Mono<ResponseEntity<Venue>> saveNew(@RequestBody Venue myEntry){

            return service.saveNew(myEntry)
                    .doOnNext(saved -> log.info("Venue saved successfully: {}", saved))
                    .doOnError(error -> log.error("Error saving Venue", error))
                    .map(x->new ResponseEntity<>(x, HttpStatus.CREATED))
                    .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    }

    @Operation(
            summary = "Fetch Venue by Id",
            description = "To fetch Venue details by Id for creating new Events"
    )
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Venue>> getById(@PathVariable String id) {
        return service.getById(new ObjectId(id))
                .map(elem -> new ResponseEntity<>(elem, HttpStatus.OK))
                .doOnError(error -> log.error("Not getting any Venue", error))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "Fetch all Venue",
            description = "To Fetch all Venue with their Amenity details and time slot"
    )
    @GetMapping("/all/{id}")
    public Mono<ResponseEntity<VenueRes>> getAllById(@PathVariable String id) {
        return service.getById(new ObjectId(id)).map(ven->{
                    return VenueRes.builder()
                            .id(ven.getId()).name(ven.getName())
                            .location(ven.getLocation()).amenities(ven.getAmenities())
                            .images(ven.getImages()).skills(ven.getSkills())
                            .availability(ven.getAvailability()).feedbacks(ven.getFeedbacks())
                            .createdOn(ven.getCreatedOn()).lastUpdatedOn(ven.getLastUpdatedOn()).build();
                        }
                )
                .flatMap(venue -> {
                    return amenityService.getByVenueId(venue.getId()).collectList()
                            .map(amenities -> {
                                venue.setAmenitiesObj(amenities); // <- set fetched Amenity objects
                                venue.setAmenities(null);         // <- remove IDs from response if you don't want to show
                                return venue;
                            });
                })
                .map(venue -> new ResponseEntity<>(venue, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "delete Venue by id",
            description = "To delete Venue if no more in use"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Update Venue",
            description = "To Update Venue and their details"
    )
    @PutMapping("/update")
    public Mono<ResponseEntity<Venue>> update(@RequestBody Mono<Venue> myEntryMono) {
        return myEntryMono
                .flatMap(service::save) // Call the service method
                .map(ResponseEntity::ok) // Return 200 OK with saved venue
                .doOnNext(saved -> log.info("Venue saved successfully: {}", saved))
                .doOnError(error -> log.error("Error saving Venue", error))
                .defaultIfEmpty(ResponseEntity.notFound().build()) // If no venue, return 404
                .onErrorResume(e -> {
                    System.err.println("Error updating venue: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

}
