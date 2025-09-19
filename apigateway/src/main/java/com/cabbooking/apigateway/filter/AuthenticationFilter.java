//package com.cabbooking.apigateway.filter;
//
//import com.cabbooking.apigateway.util.JwtUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//@Slf4j
//public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    public AuthenticationFilter() {
//        super(Config.class);
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return ((exchange, chain) -> {
//            ServerHttpRequest request = exchange.getRequest();
//
//            // Check if the path is in the excluded list (public endpoints)
//            if (isPublicEndpoint(request.getURI().getPath())) {
//                return chain.filter(exchange);
//            }
//
//            // Check if Authorization header exists
//            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
//                return handleUnauthorized(exchange, "Missing Authorization header");
//            }
//
//            String authHeader = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).get(0);
//
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                return handleUnauthorized(exchange, "Invalid Authorization header format");
//            }
//
//            String token = authHeader.substring(7);
//
//            try {
//                if (!jwtUtil.validateToken(token)) {
//                    return handleUnauthorized(exchange, "Invalid JWT token");
//                }
//
//                if (jwtUtil.isTokenExpired(token)) {
//                    return handleUnauthorized(exchange, "JWT token has expired");
//                }
//
//                // Add user info to request headers for downstream services
//                String username = jwtUtil.getUsernameFromToken(token);
//                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
//                        .header("X-User-Email", username)
//                        .build();
//
//                return chain.filter(exchange.mutate().request(modifiedRequest).build());
//
//            } catch (Exception e) {
//                log.error("JWT token validation error: {}", e.getMessage());
//                return handleUnauthorized(exchange, "Token validation failed");
//            }
//        });
//    }
//
//    private boolean isPublicEndpoint(String path) {
//        List<String> publicEndpoints = Arrays.asList(
//                "/api/auth/login",
//                "/api/auth/register",
//                "/api/drivers/register",
//                "/api/users/register",
//                "/api/locations/all",
//                "/actuator/health"
//        );
//
//        return publicEndpoints.stream().anyMatch(path::startsWith);
//    }
//
//    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
//        log.warn("Unauthorized access attempt: {}", message);
//        ServerHttpResponse response = exchange.getResponse();
//        response.setStatusCode(HttpStatus.UNAUTHORIZED);
//        response.getHeaders().add("Content-Type", "application/json");
//
//        String body = "{\"error\":\"Unauthorized\",\"message\":\"" + message + "\"}";
//
//        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
//    }
//
//    public static class Config {
//        // Configuration properties can be added here if needed
//    }
//}
