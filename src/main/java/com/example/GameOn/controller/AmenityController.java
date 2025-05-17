package com.example.GameOn.controller;

import com.example.GameOn.entity.Amenity;
import com.example.GameOn.entity.TimeSlots;
import com.example.GameOn.service.AmenityService;
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
@RequestMapping("/api/amenity")
@Tag(name = "Amenity Controller", description = "Manage Amenity")
public class AmenityController {

    @Autowired
    AmenityService service;

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
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minPrice,
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
        if (Objects.nonNull(maxPrice)) {
            filterMap.put("maxPrice", maxPrice);
        }
        if (Objects.nonNull(minPrice)) {
            filterMap.put("minPrice", minPrice);
        }

        return service.getFilteredList(filterMap, page, size, sortBy, sortOrder)
                .collectList() // Convert Flux to Mono<List<Amenity>>
                .flatMap(amenities -> {
                    if (amenities.isEmpty()) {
                        System.out.println("No Amenity found.");
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                    } else {
                        return Mono.just(ResponseEntity.ok(amenities));
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Error fetching Amenity: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @Operation(
            summary = "Create New Amenity",
            description = "To create new Amenity it is use in creating new Event"
    )
    @PostMapping("/add")
    public Mono<ResponseEntity<Amenity>> saveNewDocument(@RequestBody Amenity myEntry) {
        log.info("Received request to save new Amenity: {}", myEntry);
        return service.saveNew(myEntry)
                .doOnNext(saved -> log.info("Amenity saved successfully: {}", saved))
                .doOnError(error -> log.error("Error saving Amenity", error))
                .map(x -> new ResponseEntity<>(x, HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @Operation(
            summary = "Fetch Amenity",
            description = "To Fetch Amenity and their details like name and time slot present."
    )
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Amenity>> getById(@PathVariable String id) {
        return service.getById(new ObjectId(id))
                .map(elem -> new ResponseEntity<>(elem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "Fetch Amenity by venueId",
            description = "To Fetch Amenity and their details like name and time slot present by venueId Associated with venueId."
    )
    @GetMapping("/by_venue/{id}")
    public Flux<ResponseEntity<Amenity>> getByVenueId(@PathVariable String id) {
        return service.getByVenueId(id)
                .map(elem -> new ResponseEntity<>(elem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "delete Amenity",
            description = "To delete Amenity no more in use"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        log.info("Received request to delete Amenity: {}", id);
        service.delete(new ObjectId(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Update Amenity",
            description = "To Update Amenity if need to update details"
    )
    @PutMapping("/update")
    public Mono<ResponseEntity<Amenity>> update(@RequestBody Mono<Amenity> myEntryMono) {
        log.info("Received request to update Amenity: {}", myEntryMono);
        return myEntryMono
                .flatMap(service::save) // Call the service method
                .doOnNext(saved -> log.info("Amenity saved successfully: {}", saved))
                .doOnError(error -> log.error("Error saving Amenity", error))
                .map(ResponseEntity::ok) // Return 200 OK with saved Amenity
                .defaultIfEmpty(ResponseEntity.notFound().build()) // If no Amenity, return 404
                .onErrorResume(e -> {
                    log.info("getting error while updating Amenity: {}", myEntryMono);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @Operation(
            summary = "Add TimeSlot in Amenity ",
            description = "To Update Amenity if need to add and update new Timeslot"
    )
    @PutMapping("/add/time_slot/{id}")
    public Mono<ResponseEntity<Amenity>> update(@PathVariable String id, @RequestBody TimeSlots myEntryMono) {
        log.info("Received request to update Amenity: {}", myEntryMono);
        return service.getById(new ObjectId(id))
                .flatMap(amenity -> {
                    amenity.getTimeSlots().add(myEntryMono);
                    return Mono.just(amenity);
                })
                .doOnNext(saved -> log.info("Amenity saved successfully: {}", saved))
                .doOnError(error -> log.error("Error saving Amenity", error))
                .map(ResponseEntity::ok) // Return 200 OK with saved Amenity
                .defaultIfEmpty(ResponseEntity.notFound().build()) // If no Amenity, return 404
                .onErrorResume(e -> {
                    log.info("getting error while updating Amenity: {}", myEntryMono);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });

    }
}
