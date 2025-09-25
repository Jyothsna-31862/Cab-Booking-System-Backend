package com.cabbooking.apigateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfiguration {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.ofDefaults();
    }

    // Removed all programmatic circuit breaker beans
    // Circuit breakers are now configured declaratively in application.yml
    // Spring Cloud Gateway will automatically create circuit breakers based on YAML configuration
}
