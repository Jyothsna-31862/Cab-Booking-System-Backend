package com.cabbooking.apigateway.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/circuit-breaker")
@RequiredArgsConstructor
public class CircuitBreakerController {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private static final String CIRCUIT_BREAKER_PREFIX = "Circuit breaker ";

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAllCircuitBreakerStatus() {
        Map<String, Object> status = new HashMap<>();

        circuitBreakerRegistry.getAllCircuitBreakers().forEach(circuitBreaker -> {
            Map<String, Object> cbStatus = new HashMap<>();
            cbStatus.put("state", circuitBreaker.getState().toString());
            cbStatus.put("failureRate", circuitBreaker.getMetrics().getFailureRate());
            cbStatus.put("numberOfBufferedCalls", circuitBreaker.getMetrics().getNumberOfBufferedCalls());
            cbStatus.put("numberOfFailedCalls", circuitBreaker.getMetrics().getNumberOfFailedCalls());
            cbStatus.put("numberOfSuccessfulCalls", circuitBreaker.getMetrics().getNumberOfSuccessfulCalls());
            cbStatus.put("slowCallRate", circuitBreaker.getMetrics().getSlowCallRate());
            cbStatus.put("numberOfSlowCalls", circuitBreaker.getMetrics().getNumberOfSlowCalls());

            status.put(circuitBreaker.getName(), cbStatus);
        });

        return ResponseEntity.ok(status);
    }

    @GetMapping("/status/{serviceName}")
    public ResponseEntity<Map<String, Object>> getCircuitBreakerStatus(@PathVariable String serviceName) {
        try {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(serviceName);
            Map<String, Object> status = new HashMap<>();

            status.put("name", circuitBreaker.getName());
            status.put("state", circuitBreaker.getState().toString());
            status.put("failureRate", circuitBreaker.getMetrics().getFailureRate());
            status.put("numberOfBufferedCalls", circuitBreaker.getMetrics().getNumberOfBufferedCalls());
            status.put("numberOfFailedCalls", circuitBreaker.getMetrics().getNumberOfFailedCalls());
            status.put("numberOfSuccessfulCalls", circuitBreaker.getMetrics().getNumberOfSuccessfulCalls());
            status.put("slowCallRate", circuitBreaker.getMetrics().getSlowCallRate());
            status.put("numberOfSlowCalls", circuitBreaker.getMetrics().getNumberOfSlowCalls());

            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Circuit breaker not found: {}", serviceName);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/reset/{serviceName}")
    public ResponseEntity<String> resetCircuitBreaker(@PathVariable String serviceName) {
        try {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(serviceName);
            circuitBreaker.reset();
            log.info("Circuit breaker {} has been reset", serviceName);
            return ResponseEntity.ok(CIRCUIT_BREAKER_PREFIX + serviceName + " has been reset");
        } catch (Exception e) {
            log.error("Failed to reset circuit breaker: {}", serviceName);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/transition/{serviceName}/close")
    public ResponseEntity<String> closeCircuitBreaker(@PathVariable String serviceName) {
        try {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(serviceName);
            circuitBreaker.transitionToClosedState();
            log.info("Circuit breaker {} has been transitioned to CLOSED state", serviceName);
            return ResponseEntity.ok(CIRCUIT_BREAKER_PREFIX + serviceName + " transitioned to CLOSED");
        } catch (Exception e) {
            log.error("Failed to close circuit breaker: {}", serviceName);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/transition/{serviceName}/open")
    public ResponseEntity<String> openCircuitBreaker(@PathVariable String serviceName) {
        try {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(serviceName);
            circuitBreaker.transitionToOpenState();
            log.info("Circuit breaker {} has been transitioned to OPEN state", serviceName);
            return ResponseEntity.ok(CIRCUIT_BREAKER_PREFIX + serviceName + " transitioned to OPEN");
        } catch (Exception e) {
            log.error("Failed to open circuit breaker: {}", serviceName);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/transition/{serviceName}/half-open")
    public ResponseEntity<String> halfOpenCircuitBreaker(@PathVariable String serviceName) {
        try {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(serviceName);
            circuitBreaker.transitionToHalfOpenState();
            log.info("Circuit breaker {} has been transitioned to HALF_OPEN state", serviceName);
            return ResponseEntity.ok(CIRCUIT_BREAKER_PREFIX + serviceName + " transitioned to HALF_OPEN");
        } catch (Exception e) {
            log.error("Failed to transition circuit breaker to half-open: {}", serviceName);
            return ResponseEntity.notFound().build();
        }
    }
}
