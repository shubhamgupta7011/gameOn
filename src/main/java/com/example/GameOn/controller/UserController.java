package com.example.GameOn.controller;

import com.example.GameOn.entity.UserDetails.MatchPreference;
import com.example.GameOn.entity.UserDetails.Users;

import com.example.GameOn.enums.*;
import com.example.GameOn.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/api/user")
@Tag(name = "User Controller", description = "Manage users")
public class UserController {

    @Autowired
    UserService service;

    @GetMapping("/all")
    public Mono<ResponseEntity<?>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(required = false) String planId,
            @RequestParam(required = false) Integer securityRating,
//            @RequestParam(required = false) Integer skillRating,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) SmokingPreference smoking,
            @RequestParam(required = false) DrinkingPreference drinking,
            @RequestParam(required = false) WorkoutPreference workout,
            @RequestParam(required = false) DietPreference diet,
            @RequestParam(required = false) SexualPreference sexualPreference,
            @RequestParam(required = false) LookingFor lookingFor,
            @RequestParam(required = false) City city,
            @RequestParam(required = false) States state,
            @RequestParam(required = false) Hobby hobby,
            @RequestParam(required = false) Language[] languages,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {

        Map<String, Object> filterMap = new HashMap<>();

        if (Objects.nonNull(skills)) {
            filterMap.put("skills", skills);
        }
        if (Objects.nonNull(isDeleted)) {
            filterMap.put("isDeleted", isDeleted);
        }
        if (Objects.nonNull(isVerified)) {
            filterMap.put("isVerified", isVerified);
        }
        if (Objects.nonNull(planId)) {
            filterMap.put("planId", planId);
        }
        if (Objects.nonNull(gender)) {
            filterMap.put("userDetails.gender", gender);
        }
        if (Objects.nonNull(securityRating)) {
            filterMap.put("userDetails.securityRating", securityRating);
        }
        if (Objects.nonNull(drinking)) {
            filterMap.put("userDetails.personalPreference.drinking", drinking);
        }
        if (Objects.nonNull(smoking)) {
            filterMap.put("userDetails.personalPreference.smoking", smoking);
        }
        if (Objects.nonNull(languages)) {
            filterMap.put("userDetails.languages", languages);
        }
        if (Objects.nonNull(hobby)) {
            filterMap.put("userDetails.hobby", hobby);
        }
        if (Objects.nonNull(workout)) {
            filterMap.put("userDetails.personalPreference.workout", workout);
        }
        if (Objects.nonNull(diet)) {
            filterMap.put("userDetails.personalPreference.diet", diet);
        }
        if (Objects.nonNull(sexualPreference)) {
            filterMap.put("userDetails.personalDetails.sexualPreference", sexualPreference);
        }
        if (Objects.nonNull(lookingFor)) {
            filterMap.put("userDetails.lookingFor", lookingFor);
        }
        if (Objects.nonNull(city)) {
            filterMap.put("userDetails.personalDetails.location.city", city);
        }
        if (Objects.nonNull(state)) {
            filterMap.put("userDetails.personalDetails.location.state", state);
        }

        return service.getFilteredList(filterMap, page, size, sortBy, sortOrder)
                .collectList() // Convert Flux to Mono<List<User>>
                .flatMap(users -> {
                    if (users.isEmpty()) {
                        log.info("No User found.");
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                    } else {
                        return Mono.just(ResponseEntity.ok(users));
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error fetching User: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping
    public Mono<ResponseEntity<Users>> saveNew(@Valid @RequestBody Users myEntry) {

        myEntry.getSubscription().setPlanId("plan_Free_7");
        myEntry.getSubscription().setPlansType(PlansType.TRIAL);
        myEntry.getSubscription().setSubscriptionStartDate(LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant(ZoneId.of("UTC").getRules().getOffset(Instant.now())).toEpochMilli());
        myEntry.getSubscription().setSubscriptionStartEndDate(LocalDateTime.now(ZoneId.of("Asia/Kolkata")).plusDays(7).toInstant(ZoneId.of("UTC").getRules().getOffset(Instant.now())).toEpochMilli());

        myEntry.setUid(service.generateHashFromPhone(myEntry.getPhoneNumber()));
        return service.saveNewUser(myEntry)
                .doOnNext(saved -> log.info("User saved successfully: {}", saved))
                .doOnError(error -> log.error("Error saving User", error))
                .map(x -> new ResponseEntity<>(x, HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Users>> getById(@PathVariable String id) {
        return service.getById(new ObjectId(id))
                .map(elem -> new ResponseEntity<>(elem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/uid/{uid}")
    public Mono<ResponseEntity<Users>> getByUid(@PathVariable String uid) {
        return service.findByUserId(uid)
                .map(elem -> new ResponseEntity<>(elem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Delete User permanently", description = "Delete user by deleting document from database")
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        service.delete(new ObjectId(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Delete user temporary", description = "Delete user by setting isDeleted true")
    @DeleteMapping("/uid/{uid}")
    public ResponseEntity<?> deleteByUId(@PathVariable String uid) {
        return new ResponseEntity<>(
                service.findByUserId(uid).map(users -> {
                    users.setIsDeleted(true);
                    return service.saveUser(users);
                }), HttpStatus.OK
        );
    }

    @Operation(summary = "update user", description = "Update User Details")
    @PutMapping
    public Mono<ResponseEntity<Users>> update(@RequestBody Mono<Users> myEntryMono) {
        return myEntryMono
                .flatMap(service::saveUser) // Call the service method
                .map(ResponseEntity::ok) // Return 200 OK with saved User
                .doOnNext(saved -> log.info("User saved successfully: {}", saved))
                .doOnError(error -> log.error("Error saving User", error))
                .defaultIfEmpty(ResponseEntity.notFound().build()) // If no User, return 404
                .onErrorResume(e -> {
                    System.err.println("Error updating User: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @Operation(summary = "update match preference", description = "Update user match preference by id")
    @PutMapping("match_preference/{id}")
    public Mono<ResponseEntity<Users>> updateUser(
            @RequestBody MatchPreference matchPreference,
            @PathVariable String uid) {

        return service.findByUserId(uid)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> {
                    user.setMatchPreference(matchPreference);
                    return service.saveUser(user);
                })
                .flatMap(savedUser -> service.findByUserId(uid))
                .map(updatedUser -> new ResponseEntity<>(updatedUser, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("match_preference/{uId}")
    public Mono<ResponseEntity<MatchPreference>> getMatchPreference(@PathVariable String uid) {
        return service.findByUserId(uid)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .doOnError(error -> log.error("User not found", error))
                .map(user -> new ResponseEntity<>(user.getMatchPreference(), HttpStatus.OK));
    }
}
