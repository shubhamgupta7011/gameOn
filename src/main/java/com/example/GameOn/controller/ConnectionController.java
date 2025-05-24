package com.example.GameOn.controller;

import com.example.GameOn.entity.Booking;
import com.example.GameOn.entity.Connection;
import com.example.GameOn.entity.ConnectionRequest;
import com.example.GameOn.enums.EventStatus;
import com.example.GameOn.enums.EventType;
import com.example.GameOn.service.BookingService;
import com.example.GameOn.service.ConnectionService;
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
@RequestMapping("/api/connection")
@Tag(name = "Booking Controller", description = "Manage Booking or Events")
public class ConnectionController {

//    @Autowired
//    ConnectionService service;
//
//
//    @Operation(
//            summary = "Create Booking",
//            description = "To fetch Amenity and their details on the bases of different filters and we can sort them of different fields"
//    )
//    @PostMapping("/request")
//    public Mono<ResponseEntity<Connection>> saveNew(@RequestBody ConnectionRequest myEntry){
//
//        return service.saveNew(myEntry)
//                .map(x->new ResponseEntity<>(x, HttpStatus.CREATED))
//                .doOnNext(saved -> log.info("Booking saved successfully: {}", saved))
//                .doOnError(error -> log.error("Error Booking booking", error))
//                .defaultIfEmpty(ResponseEntity.badRequest().build());
//
//    }
//
//    @Operation(
//            summary = "Fetch Booking By Id", description = "Fetch Booking By Id"
//    )
//    @GetMapping("/respond")
//    public Flux<ResponseEntity<Connection>> getById(@PathVariable String uid) {
////        log.info("Received request to delete Booking: {}", uid);
////        return service.getByUserId(uid)
////                .map(elem -> new ResponseEntity<>(elem, HttpStatus.OK))
////                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }
//
//    @Operation(
//            summary = "Delete Connection",
//            description = "To Delete Connection"
//    )
//    @DeleteMapping("/{uid}")
//    public ResponseEntity<?> deleteById(@PathVariable String id) {
//        service.delete(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }

}
