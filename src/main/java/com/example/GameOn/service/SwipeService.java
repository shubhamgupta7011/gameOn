package com.example.GameOn.service;

import com.example.GameOn.entity.MatchKey;
import com.example.GameOn.entity.Matches;
import com.example.GameOn.entity.SwipeBySwipee;
import com.example.GameOn.entity.SwipeByUser;
import com.example.GameOn.enums.SwipeType;
import com.example.GameOn.repository.MatchesByUserRepository;
import com.example.GameOn.repository.SwipeBySwipeeRepository;
import com.example.GameOn.repository.SwipeByUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Service
@Slf4j
public class SwipeService {

    @Autowired
    private SwipeByUserRepository userRepo;

    @Autowired
    private SwipeBySwipeeRepository swipeeRepo;

    @Autowired
    private MatchesByUserRepository matchRepo;

    public Mono<Void> saveSwipe(String swiperId, String swipeeId, SwipeType swipe_type) {
        Instant now = Instant.now();

        SwipeByUser swipeByUser = new SwipeByUser();
        swipeByUser.setKey(new SwipeByUser.Key(swiperId, swipeeId));
        swipeByUser.setSwipe_type(swipe_type);
        swipeByUser.setTimestamp(now.toString());

        SwipeBySwipee swipeBySwipee = new SwipeBySwipee();
        swipeBySwipee.setKey(new SwipeBySwipee.Key(swipeeId, swiperId));
        swipeBySwipee.setSwipeType(swipe_type);
        swipeBySwipee.setTimestamp(now.toString());

        return userRepo.save(swipeByUser)
                .then(swipeeRepo.save(swipeBySwipee))
                .then();
    }

//    public Mono<Boolean> isMatch(String swiperId, String swipeeId) {
//        Mono<SwipeByUser> swipe = userRepo.findById(new SwipeByUser.Key(swiperId, swipeeId));
//        Mono<SwipeBySwipee> reverse = swipeeRepo.findById(new SwipeBySwipee.Key(swiperId, swipeeId));
//
//        return Mono.zip(swipe, reverse)
//                .map(tuple ->
//                        "RIGHT".equals(tuple.getT1().getSwipe_type().toString())
//                                && "RIGHT".equals(tuple.getT2().getSwipeType().toString())
//                ).defaultIfEmpty(false);
//    }

    public Mono<Boolean> isMatch(String swiperId, String swipeeId) {
        Mono<SwipeByUser> swipe = userRepo.findById(new SwipeByUser.Key(swiperId, swipeeId));
        Mono<SwipeBySwipee> reverse = swipeeRepo.findById(new SwipeBySwipee.Key(swiperId, swipeeId));

        return Mono.zip(swipe, reverse)
                .flatMap(tuple -> {
                    boolean isMutualRight = "RIGHT".equals(tuple.getT1().getSwipe_type().toString()) &&
                            "RIGHT".equals(tuple.getT2().getSwipeType().toString());

                    if (isMutualRight) {
                        String timestamp = Instant.now().toString();
                        Matches match = new Matches(swipeeId, swiperId, timestamp);
                        Matches match2 = new Matches(swiperId, swipeeId, timestamp);
                        return matchRepo.save(match).then(matchRepo.save(match2))
                                .doOnError(ex -> log.error(ex.getMessage()))
                                .thenReturn(true);
                    } else {
                        return Mono.just(false);
                    }
                })
                .defaultIfEmpty(false);
    }


    public Flux<SwipeByUser> getSwipesByUser(String userId) {
        return userRepo.findByKeySwiperId(userId);
    }

    public Flux<SwipeBySwipee> getUsersWhoSwipedOnMe(String userId) {
        return swipeeRepo.findByKeySwipeeId(userId);
    }

    public Flux<SwipeBySwipee> getUsersWhoSwipedOnMe(String userId, SwipeType swipe_type) {
        return swipeeRepo.findByKeySwipeeIdAndSwipeType(userId,swipe_type);
    }

    public Flux<Matches> findByKeyUserId(String userId) {
        return matchRepo.findByKeyUserId(userId);
    }
}

