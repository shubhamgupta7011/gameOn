package com.example.GameOn.controller;

import com.example.GameOn.entity.PlansAndOffers;
import com.example.GameOn.entity.UserDetails.MatchPreference;
import com.example.GameOn.entity.UserDetails.UserProfile;
import com.example.GameOn.service.MatchPrefrenceService;
import com.example.GameOn.service.PlansService;
import com.example.GameOn.utils.Utility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/api/match_preference")
@Tag(name = "Match Preference Controller", description = "Manage Match Preference")
public class MatchPrefrenceController {

    @Autowired
    MatchPrefrenceService service;

    @Operation(
            summary = "update match preference",
            description = "Update user match preference by id"
    )
    @PutMapping("/update")
    public Mono<ResponseEntity<MatchPreference>> updateUser(@RequestBody MatchPreference request) {
        String uid = request.getId();
        return service.getByUserId(uid)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(matchPreference -> {
                    matchPreference = request;
                    matchPreference.setLastUpdatedOn(Utility.getCurrentTime());
                    return service.save(matchPreference);
                })
                .map(updatedUser -> new ResponseEntity<>(updatedUser, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "Get match preference",
            description = "To get match preferences of User by uid"
    )
    @GetMapping("/{uid}")
    public Mono<ResponseEntity<MatchPreference>> getMatchPreference(@PathVariable String uid) {
        return service.getByUserId(uid)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .doOnError(error -> log.error("User not found", error))
                .map(match -> new ResponseEntity<>(match, HttpStatus.OK));
    }
}
