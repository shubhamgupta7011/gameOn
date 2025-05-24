package com.example.GameOn.controller;

import com.example.GameOn.client.NominatimApiClient;
import com.example.GameOn.enums.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/utils")
@Tag(name = "util Controller", description = "Manage app controller")
public class UtilsController {

    private final NominatimApiClient nominatimApiClient;

    public UtilsController(NominatimApiClient nominatimApiClient) {
        this.nominatimApiClient = nominatimApiClient;
    }

    @Operation(
            summary = "Fetch the location details",
            description = "To Fetch the location details by latitude and longitude"
    )
    @GetMapping("/location/search/{lat}/{lon}")
    public Mono<ResponseEntity<String>> searchLocation(@PathVariable Double lat, @PathVariable Double lon) {
        return nominatimApiClient.getLocationDetails(lat, lon)
                .map(response -> {
                    System.out.println("✅ [Final Response Body]: " + response); // Log final response
                    return ResponseEntity.ok()
                            .header("Content-Type", "application/json")
                            .body(response);
                });
    }

    @Operation(
            summary = "Fetch all the Enums",
            description = "To fetch all the Enums like City, State, BodyType and etc for Ui configs "
    )
    @GetMapping("/config")
    public Mono<ResponseEntity<Map<String, Object>>> getAllConfig() {
        return Mono.fromSupplier(() -> {
            Map<String, Object> res = new HashMap<>();

            res.put("bodyType", Arrays.stream(BodyType.values())
                    .collect(Collectors.toMap(BodyType::name, BodyType::getDescription)));

            res.put("city", Arrays.stream(City.values())
                    .collect(Collectors.toMap(City::name, City::getDescriptions)));

            res.put("dietPreference", Arrays.stream(DietPreference.values())
                    .collect(Collectors.toMap(DietPreference::name, DietPreference::getDescription)));

            res.put("drinkingPreference", Arrays.stream(DrinkingPreference.values())
                    .collect(Collectors.toMap(DrinkingPreference::name, DrinkingPreference::getDescription)));

            res.put("educationLevel", Arrays.stream(EducationLevel.values())
                    .collect(Collectors.toMap(EducationLevel::name, EducationLevel::getDescription)));

            res.put("gender", Arrays.stream(Gender.values())
                    .collect(Collectors.toMap(Gender::name, Gender::getDescription)));

            res.put("hobby", Arrays.stream(Hobby.values())
                    .collect(Collectors.toMap(Hobby::name, Hobby::getDescription)));

            res.put("interestedIn", Arrays.stream(InterestedIn.values())
                    .collect(Collectors.toMap(InterestedIn::name, InterestedIn::getDescription)));

            res.put("language", Arrays.stream(Language.values())
                    .collect(Collectors.toMap(Language::name, Language::getDisplayName)));

            res.put("lookingFor", Arrays.stream(LookingFor.values())
                    .collect(Collectors.toMap(LookingFor::name, LookingFor::getDescription)));

            res.put("sexualPreference", Arrays.stream(SexualPreference.values())
                    .collect(Collectors.toMap(SexualPreference::name, SexualPreference::getDescription)));

            res.put("skills", Arrays.stream(Skills.values())
                    .collect(Collectors.toMap(Skills::name, Skills::getDisplayName)));

            res.put("sleepHabits", Arrays.stream(SleepHabits.values())
                    .collect(Collectors.toMap(SleepHabits::name, SleepHabits::getDescription)));

            res.put("smokingPreference", Arrays.stream(SmokingPreference.values())
                    .collect(Collectors.toMap(SmokingPreference::name, SmokingPreference::getDescription)));

            res.put("socialMediaActivity", Arrays.stream(SocialMediaActivity.values())
                    .collect(Collectors.toMap(SocialMediaActivity::name, SocialMediaActivity::getDescription)));

            res.put("states", Arrays.stream(States.values())
                    .collect(Collectors.toMap(States::name, States::getDescriptions)));

            res.put("workoutPreference", Arrays.stream(WorkoutPreference.values())
                    .collect(Collectors.toMap(WorkoutPreference::name, WorkoutPreference::getDescription)));

            res.put("timeSlotStatus", Arrays.stream(TimeSlotStatus.values())
                    .collect(Collectors.toMap(TimeSlotStatus::name, TimeSlotStatus::getDescription)));

            res.put("plansType", Arrays.stream(PlansType.values())
                    .collect(Collectors.toMap(PlansType::name, PlansType::getDescription)));

            return ResponseEntity.ok(res);
        });
    }
}
