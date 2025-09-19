package com.cabbooking.apigateway.config;

import com.cabbooking.apigateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service Routes (Public - no authentication needed)
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri("lb://AUTH-SERVICE"))

                // User Service Routes (Protected)
                .route("user-service", r -> r.path("/api/users/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://USER-SERVICE"))

                // Driver Service Routes (Protected)
                .route("driver-service", r -> r.path("/api/drivers/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://DRIVER-SERVICE"))

                // Ride Service Routes (Protected)
                .route("ride-service", r -> r.path("/api/rides/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://RIDE-SERVICE"))

                // Rating Service Routes (Protected)
                .route("rating-service", r -> r.path("/api/ratings/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://RATING-SERVICE"))

                // Location Service Routes (Protected)
                .route("location-service", r -> r.path("/api/locations/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://LOCATION-SERVICE"))

                // Payment Service Routes (Protected)
                .route("payment-service", r -> r.path("/api/payments/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://PAYMENT-SERVICE"))

                .build();
    }
}
