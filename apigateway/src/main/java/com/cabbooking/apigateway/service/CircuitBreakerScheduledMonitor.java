package com.cabbooking.apigateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CircuitBreakerScheduledMonitor {

    private final CircuitBreakerMonitoringService monitoringService;

    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void monitorCircuitBreakers() {
        log.debug("=== Circuit Breaker Health Check ===");
        monitoringService.logCircuitBreakerMetrics();

        // Check for open circuit breakers
        var openCircuitBreakers = monitoringService.getOpenCircuitBreakers();
        if (!openCircuitBreakers.isEmpty()) {
            log.warn("ALERT: The following circuit breakers are OPEN: {}", openCircuitBreakers);
        }

        // Check for half-open circuit breakers
        var halfOpenCircuitBreakers = monitoringService.getHalfOpenCircuitBreakers();
        if (!halfOpenCircuitBreakers.isEmpty()) {
            log.info("Circuit breakers in HALF_OPEN state (testing): {}", halfOpenCircuitBreakers);
        }
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void logSystemHealth() {
        var allStates = monitoringService.getAllCircuitBreakerStates();
        log.info("=== System Health Summary ===");
        allStates.forEach((service, state) ->
            log.info("Service: {} - Circuit Breaker State: {}", service, state));
    }
}
