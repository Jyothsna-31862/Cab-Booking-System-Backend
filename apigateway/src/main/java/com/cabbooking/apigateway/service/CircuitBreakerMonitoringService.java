package com.cabbooking.apigateway.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CircuitBreakerMonitoringService {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public Map<String, CircuitBreaker.State> getAllCircuitBreakerStates() {
        return circuitBreakerRegistry.getAllCircuitBreakers()
                .stream()
                .collect(Collectors.toMap(
                    CircuitBreaker::getName,
                    CircuitBreaker::getState
                ));
    }

    public List<String> getOpenCircuitBreakers() {
        return circuitBreakerRegistry.getAllCircuitBreakers()
                .stream()
                .filter(cb -> cb.getState() == CircuitBreaker.State.OPEN)
                .map(CircuitBreaker::getName)
                .toList();
    }

    public List<String> getHalfOpenCircuitBreakers() {
        return circuitBreakerRegistry.getAllCircuitBreakers()
                .stream()
                .filter(cb -> cb.getState() == CircuitBreaker.State.HALF_OPEN)
                .map(CircuitBreaker::getName)
                .toList();
    }

    public boolean isServiceAvailable(String serviceName) {
        try {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(serviceName);
            return circuitBreaker.getState() != CircuitBreaker.State.OPEN;
        } catch (Exception e) {
            log.warn("Circuit breaker not found for service: {}", serviceName);
            return true;
        }
    }

    public void logCircuitBreakerMetrics() {
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(circuitBreaker -> {
            var metrics = circuitBreaker.getMetrics();
            log.info("Circuit Breaker [{}] - State: {}, Failure Rate: {}%, Buffered Calls: {}, Failed: {}, Slow: {}",
                circuitBreaker.getName(),
                circuitBreaker.getState(),
                String.format("%.2f", metrics.getFailureRate()),
                metrics.getNumberOfBufferedCalls(),
                metrics.getNumberOfFailedCalls(),
                metrics.getNumberOfSlowCalls()
            );
        });
    }
}
