package com.example.GameOn.controller;

import com.example.GameOn.entity.Connection;
import com.example.GameOn.entity.ConnectionRequest;
import com.example.GameOn.service.ConnectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/api/connection")
@Tag(name = "Booking Controller", description = "Manage Booking or Events")
public class ConnectionController {

    @Autowired
    ConnectionService service;

    @Operation(
            summary = "Create Connection Request",
            description = "To fetch Amenity and their details on the bases of different filters and we can sort them of different fields"
    )
    @PostMapping("/request")
    public Mono<ResponseEntity<ConnectionRequest>> saveNew(@RequestBody ConnectionRequest myEntry){

        return service.saveNew(myEntry)
                .map(x->new ResponseEntity<>(x, HttpStatus.CREATED))
                .doOnNext(saved -> log.info("Booking saved successfully: {}", saved))
                .doOnError(error -> log.error("❌Error Booking booking", error))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Operation(
            summary = "Respond on Connection request",
            description = "To update Connection request by respond by another user"
    )
    @PutMapping("/respond")
    public Mono<ResponseEntity<ConnectionRequest>> update(@RequestBody Mono<ConnectionRequest> myEntryMono) {
        return myEntryMono
                .flatMap(service::save) // Call the service method
                .map(ResponseEntity::ok) // Return 200 OK with saved booking
                .doOnNext(saved -> log.info("Connection request respond successfully: {}", saved))
                .doOnError(error -> log.error("❌ Error in Connection request response", error))
                .defaultIfEmpty(ResponseEntity.notFound().build()) // If no booking, return 404
                .onErrorResume(e -> {
                    log.error("❌ Error updating Connection request response: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @Operation(
            summary = "Connection List",
            description = "To update Connection request by respond another user"
    )
    @GetMapping("/list/{userId}")
    public Mono<ResponseEntity<Connection>> getList(@PathVariable String userId) {
        return service.getByUserId(userId)
                .map(ResponseEntity::ok) // Return 200 OK with saved booking
                .doOnNext(saved -> log.info("Connection List: {}", saved))
                .doOnError(error -> log.error("❌ Error in Connection request response", error))
                .defaultIfEmpty(ResponseEntity.notFound().build()); // If no booking, return 404;
    }

    @Operation(
            summary = "Connection List",
            description = "To update Connection request by respond another user"
    )
    @GetMapping("/{toUserId}/{fromUserId}")
    public Mono<ResponseEntity<ConnectionRequest>> alreadyPresent(@PathVariable String toUserId, @PathVariable String fromUserId) {
        return service.alreadyRequest(toUserId,fromUserId)
                .map(ResponseEntity::ok) // Return 200 OK with saved booking
                .doOnNext(saved -> log.info("Connection List: {}", saved))
                .doOnError(error -> log.error("❌ Error in Connection request response", error))
                .defaultIfEmpty(ResponseEntity.notFound().build()); // If no booking, return 404;
    }

    @Operation(
            summary = "Pending Request List",
            description = "To update Connection request by respond another user"
    )
    @GetMapping("/list/pending/{fromUserId}")
    public Flux<ResponseEntity<ConnectionRequest>> listOfRendingRequest(@PathVariable String fromUserId) {
        return service.pendingRequestList(fromUserId)
                .map(ResponseEntity::ok) // Return 200 OK with saved booking
                .doOnNext(saved -> log.info("Connection List: {}", saved))
                .doOnError(error -> log.error("❌ Error in Connection request response", error))
                .defaultIfEmpty(ResponseEntity.notFound().build()); // If no booking, return 404;
    }

    @Operation(
            summary = "Pending Request List",
            description = "To update Connection request by respond another user"
    )
    @GetMapping("/list/send/{toUserId}")
    public Flux<ResponseEntity<ConnectionRequest>> listOfSendRequest(@PathVariable String toUserId) {
        return service.sendRequestList(toUserId)
                .map(ResponseEntity::ok) // Return 200 OK with saved booking
                .doOnNext(saved -> log.info("Connection List: {}", saved))
                .doOnError(error -> log.error("❌ Error in Connection request response", error))
                .defaultIfEmpty(ResponseEntity.notFound().build()); // If no booking, return 404;
    }
}
