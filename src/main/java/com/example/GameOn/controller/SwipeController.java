package com.example.GameOn.controller;

import com.example.GameOn.entity.Matches;
import com.example.GameOn.entity.SwipeBySwipee;
import com.example.GameOn.entity.SwipeByUser;
import com.example.GameOn.enums.SwipeType;
import com.example.GameOn.repository.MatchesByUserRepository;
import com.example.GameOn.service.SwipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/swipes")
@Tag(name = "Swipes Controller", description = "Manage swipes by user")
public class SwipeController {

    @Autowired
    private SwipeService swipeService;

    @Autowired
    private MatchesByUserRepository matchRepo;

    @Operation(
            summary = "Swipe Action",
            description = "Create Swipe data when user Swipe left or right"
    )
    @PostMapping("/add")
    public Mono<ResponseEntity<Map<String, Boolean>>> swipe(
            @RequestParam String swiperId, @RequestParam String swipeeId, @RequestParam SwipeType swipeType
    ) {

        return swipeService.saveSwipe(swiperId, swipeeId, swipeType)
                .then(swipeService.isMatch(swiperId, swipeeId))
                .map(isMatch -> ResponseEntity.ok(Map.of("match", isMatch)));
    }

    @Operation(
            summary = "Fetch all the Swiped Users",
            description = "To fetch all the Swiped Users whom I swiped right"
    )
    @GetMapping("/{userId}")
    public Flux<SwipeByUser> getSwipes(@PathVariable String userId) {
        return swipeService.getSwipesByUser(userId);
    }

    @Operation(
            summary = "Fetch All Potential match",
            description = "To Fetch All Potential match who swipe me right"
    )
    @GetMapping("/swiped-on-me/{userId}/{swipeType}")
    public Flux<SwipeBySwipee> getUsersWhoSwipedOnMe(@PathVariable String userId, @PathVariable SwipeType swipeType) {
        return swipeService.getUsersWhoSwipedOnMe(userId, swipeType);
    }

    @Operation(
            summary = "Fetch all Matches",
            description = "To Fetch all Matches of the user by uid"
    )
    @GetMapping("/matches/{userId}")
    public Flux<Matches> getMatches(@PathVariable String userId) {
        return swipeService.findByKeyUserId(userId);
    }

}
