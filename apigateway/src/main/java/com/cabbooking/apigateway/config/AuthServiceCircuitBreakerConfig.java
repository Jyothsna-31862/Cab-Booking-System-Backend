package com.cabbooking.apigateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class AuthServiceCircuitBreakerConfig {

    @Bean
    public CircuitBreaker authServiceCircuitBreakerCustom(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                // Auth service specific configuration - more strict than other services
                .failureRateThreshold(60) // Higher threshold as auth is critical
                .waitDurationInOpenState(Duration.ofSeconds(15)) // Longer wait for auth service
                .slidingWindowSize(15) // Larger window for auth service
                .minimumNumberOfCalls(8) // More calls before evaluating
                .permittedNumberOfCallsInHalfOpenState(5) // More test calls in half-open
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .slowCallRateThreshold(60)
                .slowCallDurationThreshold(Duration.ofSeconds(3)) // Stricter timeout for auth
                .enableAutomaticTransitionFromOpenToHalfOpen()
                .recordExceptions(Exception.class)
                .build();

        CircuitBreaker circuitBreaker = registry.circuitBreaker("auth-service-custom", config);

        // Add event listeners for auth service
        circuitBreaker.getEventPublisher()
                .onStateTransition(event ->
                    log.info("[AUTH-SERVICE CIRCUIT BREAKER] State transition from {} to {}",
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()))
                .onFailureRateExceeded(event ->
                    log.error("[AUTH-SERVICE CIRCUIT BREAKER] Failure rate exceeded: {}%",
                        event.getFailureRate()))
                .onCallNotPermitted(event ->
                    log.warn("[AUTH-SERVICE CIRCUIT BREAKER] Call not permitted - circuit breaker is OPEN"))
                .onError(event ->
                    log.error("[AUTH-SERVICE CIRCUIT BREAKER] Error occurred: {}",
                        event.getThrowable().getMessage()));

        return circuitBreaker;
    }
}
