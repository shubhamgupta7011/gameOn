package com.example.GameOn.controller;

import com.example.GameOn.client.NominatimApiClient;
import com.example.GameOn.entity.UserDetails.MatchPreference;
import com.example.GameOn.entity.UserDetails.UserProfile;

import com.example.GameOn.enums.*;
import com.example.GameOn.service.UserService;
import com.example.GameOn.utils.Utility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.json.JSONObject;
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

    private final NominatimApiClient nominatimApiClient;

    public UserController(NominatimApiClient nominatimApiClient) {
        this.nominatimApiClient = nominatimApiClient;
    }

    @Operation(
            summary = "Fetch all Users",
            description = "To fetch users and their details on the bases of different filters and we can sort them of different fields"
    )
    @GetMapping("/all")
    public Mono<ResponseEntity<?>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Skills skills,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(required = false) String planId,
            @RequestParam(required = false) Integer securityRating,
//            @RequestParam(required = false) Integer skillRating,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
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

        if (Objects.nonNull(minAge)) {
            filterMap.put("minAge", minAge);
        }
        if (Objects.nonNull(maxAge)) {
            filterMap.put("maxAge", maxAge);
        }
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
            filterMap.put("subscription.planId", planId);
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

    @Operation(
            summary = "Create New User",
            description = "To create new Users it is use in setup profile Journey"
    )
    @PostMapping("/add")
    public Mono<ResponseEntity<UserProfile>> saveNew(@Valid @RequestBody UserProfile myEntry) {

        nominatimApiClient.getLocationDetails(myEntry.getLocation().getLatitude(), myEntry.getLocation().getLongitude())
                .map(response -> {
                    try {
                        // Convert raw response to JSONObject
                        JSONObject jsonResponse = new JSONObject(response);
                        // Extract city, state, and address from the JSON response
                        String city = jsonResponse.optString("address.city", "Unknown City");
                        String state = jsonResponse.optString("address.state", "Unknown State");
                        String address = jsonResponse.optString("display_name", "Unknown Address");
                        String pinCode = jsonResponse.optString("address.postcode", "Unknown Address");

                        // Now set the address fields in the userDetail object
                        myEntry.getLocation().setCity(City.getDescriptionToCitiesMap().get(city).get(0));
                        myEntry.getLocation().setState(States.getDescriptionToStatesMap().get(state).get(0));
                        myEntry.getLocation().setAddress(address);
                        myEntry.getLocation().setPinCode(pinCode);

                        // Proceed with user creation logic here, for example saving to DB
                        return "User created with Address: " + address + ", " + city + ", " + state;

                    } catch (Exception e) {
                        log.error("❌ [PARSE ERROR] " + e.getMessage());
//                        e.printStackTrace();
                        return "Error while processing address.";
                    }
                });

        myEntry.getSubscription().setPlanId("plan_Free_7");
        myEntry.getSubscription().setPlansType(PlansType.TRIAL);
        myEntry.getSubscription().setSubscriptionStartDate(Utility.getCurrentTime());
        myEntry.getSubscription().setSubscriptionStartEndDate(Utility.getCurrentTime());

        myEntry.setUid(service.generateHashFromPhone(myEntry.getPhoneNumber()));
        return service.saveNewUser(myEntry)
                .doOnNext(saved -> log.info("User saved successfully: {}", saved))
                .doOnError(error -> log.error("Error saving User", error))
                .map(x -> new ResponseEntity<>(x, HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @Operation(
            summary = "Fetch user by id",
            description = "To Fetch user details in checking User Profile journey"
    )
    @GetMapping("{id}")
    public Mono<ResponseEntity<UserProfile>> getById(@PathVariable String id) {
        return service.getById(new ObjectId(id))
                .map(elem -> new ResponseEntity<>(elem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "Fetch user by uid",
            description = "To Fetch user details in checking User Profile journey"
    )
    @GetMapping("/uid/{uid}")
    public Mono<ResponseEntity<UserProfile>> getByUid(@PathVariable String uid) {
        return service.findByUserId(uid)
                .map(elem -> new ResponseEntity<>(elem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "Delete User permanently",
            description = "Delete user by deleting document from database it is used by admin"
    )
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        service.delete(new ObjectId(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Delete user temporary",
            description = "Delete user by setting isDeleted true it is use by user, when user want to delete Account by them self"
    )
    @DeleteMapping("/uid/{uid}")
    public ResponseEntity<?> deleteByUId(@PathVariable String uid) {
        return new ResponseEntity<>(
                service.findByUserId(uid).map(users -> {
                    users.setIsDeleted(true);
                    return service.saveUser(users);
                }), HttpStatus.OK
        );
    }

    @Operation(
            summary = "update user",
            description = "To Update User Details in Edit user details journey"
    )
    @PutMapping
    public Mono<ResponseEntity<UserProfile>> update(@RequestBody Mono<UserProfile> myEntryMono) {
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

    @Operation(
            summary = "update match preference",
            description = "Update user match preference by id"
    )
    @PutMapping("match_preference/{uid}")
    public Mono<ResponseEntity<UserProfile>> updateUser(
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

    @Operation(
            summary = "Get match preference",
            description = "To get match preferences of User by uid"
    )
    @GetMapping("match_preference/{uid}")
    public Mono<ResponseEntity<MatchPreference>> getMatchPreference(@PathVariable String uid) {
        return service.findByUserId(uid)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .doOnError(error -> log.error("User not found", error))
                .map(user -> new ResponseEntity<>(user.getMatchPreference(), HttpStatus.OK));
    }

    @Operation(
            summary = "Update User Location",
            description = "It is Use to update user location"
    )
    @PutMapping("/update-location/{uid}")
    public Mono<UserProfile> updateLocation(
            @PathVariable("uid") String userId, @RequestParam double lat, @RequestParam double lon
    ) {
        service.findByUserId(userId).flatMap(user -> {
            user.getLocation().setLongitude(lon);
            user.getLocation().setLatitude(lat);
            nominatimApiClient.getLocationDetails(lat, lon)
                    .map(response -> {
                        JSONObject jsonResponse = new JSONObject(response);

                        String city = jsonResponse.optString("address.city", "Unknown City");
                        String state = jsonResponse.optString("address.state", "Unknown State");
                        String address = jsonResponse.optString("display_name", "Unknown Address");
                        String pinCode = jsonResponse.optString("address.postcode", "Unknown Address");

                        user.getLocation().setCity(City.getDescriptionToCitiesMap().get(city).get(0));
                        user.getLocation().setState(States.getDescriptionToStatesMap().get(state).get(0));
                        user.getLocation().setAddress(address);
                        user.getLocation().setPinCode(pinCode);
                        return "User created with Address: " + address + ", " + city + ", " + state;
                    });
            return service.saveUser(user);
        });

        return service.updateUserLocation(userId, lat, lon);
    }

    @Operation(
            summary = "Generate User Id",
            description = "To Generate UserId/uid by Phone number"
    )
    @GetMapping("/generateUserId/{number}")
    public Mono<ResponseEntity<String>> generateUserId(@PathVariable String number) {
        return Mono.fromSupplier(() -> {
            String userId = service.generateHashFromPhone(number);
            log.info("Generating userId {} from phone: {}", userId, number);
            return ResponseEntity.ok(userId);
        });
    }


    @Operation(
            summary = "Fetch Phone Number",
            description = "To Fetch Phone Number by using UserId/uid"
    )
    @GetMapping("/phone_by_uid/{uid}")
    public Mono<ResponseEntity<String>> getPhoneByUid(@PathVariable String uid) {
        return service.findByUserId(uid)
                .map(user -> ResponseEntity.ok(user.getPhoneNumber()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
