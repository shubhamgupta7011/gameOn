package com.example.GameOn.controller;

import com.example.GameOn.entity.UserDetails.UserProfile;
import com.example.GameOn.enums.*;
import com.example.GameOn.service.LocationService;
import com.example.GameOn.service.SwipeService;
import com.example.GameOn.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@Slf4j
@RequestMapping("/api/discovery")
@Tag(name = "discovery Controller", description = "Fetch User Profiles for Swap")
public class DiscoveryController {

    @Autowired
    UserService userService;

    @Autowired
    private SwipeService swipeService;

    @Autowired
    private LocationService locationService;

    public DiscoveryController() {
    }

    @Operation(
            summary = "Fetch Users to match",
            description = "To fetch users and their details for matching on the basis of different conditations not already Swiped or Match, same Location and with in radius"
    )
    @GetMapping("/{userId}")
    public Mono<ResponseEntity<?>> getAll(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Skills skills,
            @RequestParam(defaultValue = "false") Boolean isDeleted,
            @RequestParam(required = false) Boolean isVerified,
//            @RequestParam(required = false) String planId,
            @RequestParam(required = false) Integer securityRating,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Integer skillRating,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) SmokingPreference smoking,
            @RequestParam(required = false) DrinkingPreference drinking,
            @RequestParam(required = false) WorkoutPreference workout,
            @RequestParam(required = false) DietPreference diet,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) Double distanceInKm,
            @RequestParam(required = false) SexualPreference sexualPreference,
            @RequestParam(required = false) String planId,
            @RequestParam(required = false) LookingFor lookingFor,
            @RequestParam(required = false) City city,
            @RequestParam(required = false) States state,
            @RequestParam(required = false) Hobby hobby,
            @RequestParam(required = false) Language[] languages,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        Map<String, Object> filterMap = new HashMap<>();

       userService.findByUserId(userId).map(users -> {
           filterMap.put("userDetails.gender",users.getMatchPreference().getInterestedIn());
           filterMap.put("minAge",users.getMatchPreference().getMinAge());
           filterMap.put("maxAge",users.getMatchPreference().getMaxAge());
           filterMap.put("latitude", users.getLocation().getLatitude()); // to pass for query composition
           filterMap.put("longitude", users.getLocation().getLongitude());
           filterMap.put("distanceInKm", users.getMatchPreference().getMaxDistance());
           return null;
       });

        if (Objects.nonNull(planId)) {
            filterMap.put("subscription.planId", planId);
        }
        if (Objects.nonNull(skills)) {
            filterMap.put("userDetails.skills", skills);
        }
        if (Objects.nonNull(isDeleted)) {
            filterMap.put("isDeleted", isDeleted);
        }
        if (Objects.nonNull(isVerified)) {
            filterMap.put("isVerified", isVerified);
        }
        if (Objects.nonNull(gender)) {
            filterMap.put("userDetails.gender", gender);
        }
        if (Objects.nonNull(skillRating)) {
            filterMap.put("userDetails.skillRating", skillRating);
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
            filterMap.put("userDetails.hobbies", hobby);
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
            filterMap.put("location.city", city);
        }
        if (Objects.nonNull(state)) {
            filterMap.put("location.state", state);
        }
        if (Objects.nonNull(minAge)) {
            filterMap.put("minAge", minAge);
        }
        if (Objects.nonNull(maxAge)) {
            filterMap.put("maxAge", maxAge);
        }
        if (Objects.nonNull(lat)) {
            filterMap.put("latitude", lat);
        }
        if (Objects.nonNull(lon)) {
            filterMap.put("longitude", lon);
        }
        if (Objects.nonNull(distanceInKm)) {
            filterMap.put("distanceInKm", distanceInKm);
        }

        List<String> userWhichAreSwipedAlready = new ArrayList<>();
        swipeService.getSwipesByUser(userId)
                .doOnNext(user -> userWhichAreSwipedAlready.add(user.getKey().getSwipeeId()))
                .subscribe();

        return userService.getFilteredList(filterMap, page, size, sortBy, sortOrder)
                .filter(user -> !userWhichAreSwipedAlready.contains(user.getUid()))
                .collectList()
                .flatMap(users -> {
                    if (users.isEmpty())
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                    else
                        return Mono.just(ResponseEntity.ok(users));
                })
                .onErrorResume(e -> {
                    log.error("Error fetching User: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @Operation(
            summary = "Fetch nearBy Users",
            description = "To fetch near by users List"
    )
    @GetMapping("/nearby")
    public Flux<UserProfile> getNearbyUsers(
            @RequestParam double lat, @RequestParam double lon,
            @RequestParam(defaultValue = "10") double radiusKm
    ) {
        return locationService.findNearbyUsers(lat, lon, radiusKm);
    }

}
