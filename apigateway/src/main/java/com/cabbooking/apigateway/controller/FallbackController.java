package com.cabbooking.apigateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/user-service")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        log.error("[CIRCUIT BREAKER] User Service is unavailable - Circuit breaker is OPEN");
        return createFallbackResponse("User Service");
    }

    @RequestMapping("/driver-service")
    public ResponseEntity<Map<String, Object>> driverServiceFallback() {
        log.error("[CIRCUIT BREAKER] Driver Service is unavailable - Circuit breaker is OPEN");
        return createFallbackResponse("Driver Service");
    }

    @RequestMapping("/ride-service")
    public ResponseEntity<Map<String, Object>> rideServiceFallback() {
        log.error("[CIRCUIT BREAKER] Ride Service is unavailable - Circuit breaker is OPEN");
        return createFallbackResponse("Ride Service");
    }

    @RequestMapping("/payment-service")
    public ResponseEntity<Map<String, Object>> paymentServiceFallback() {
        log.error("[CIRCUIT BREAKER] Payment Service is unavailable - Circuit breaker is OPEN");
        return createFallbackResponse("Payment Service");
    }

    @RequestMapping("/rating-service")
    public ResponseEntity<Map<String, Object>> ratingServiceFallback() {
        log.error("[CIRCUIT BREAKER] Rating Service is unavailable - Circuit breaker is OPEN");
        return createFallbackResponse("Rating Service");
    }

    @RequestMapping("/location-service")
    public ResponseEntity<Map<String, Object>> locationServiceFallback() {
        log.error("[CIRCUIT BREAKER] Location Service is unavailable - Circuit breaker is OPEN");
        return createFallbackResponse("Location Service");
    }

    @RequestMapping("/auth-service")
    public ResponseEntity<Map<String, Object>> authServiceFallback() {
        log.error("[CIRCUIT BREAKER] Auth Service is unavailable - Circuit breaker is OPEN");
        return createFallbackResponse("Auth Service");
    }

    private ResponseEntity<Map<String, Object>> createFallbackResponse(String serviceName) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "SERVICE_UNAVAILABLE");
        response.put("message", serviceName + " is temporarily unavailable due to circuit breaker being open");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
