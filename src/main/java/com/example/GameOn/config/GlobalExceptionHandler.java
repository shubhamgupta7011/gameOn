package com.example.GameOn.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

//@ControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(WebExchangeBindException.class)
//    public Mono<ResponseEntity<Map<String, String>>> handleValidationExceptions(WebExchangeBindException ex) {
//        Map<String, String> errors = new HashMap<>();
//
//        ex.getFieldErrors().forEach(error ->
//                errors.put(error.getField(), error.getDefaultMessage())
//        );
//
//        return Mono.just(ResponseEntity.badRequest().body(errors));
//    }

//    @ExceptionHandler(WebExchangeBindException.class)
//    public Mono<Map<String, Object>> handleValidationExceptions(WebExchangeBindException ex) {
//        Map<String, Object> errors = new HashMap<>();
//        ex.getFieldErrors().forEach(error ->
//                errors.put(error.getField(), error.getDefaultMessage())
//        );
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", HttpStatus.BAD_REQUEST.value());
//        response.put("errors", errors);
//        return Mono.just(response);
//    }
}
