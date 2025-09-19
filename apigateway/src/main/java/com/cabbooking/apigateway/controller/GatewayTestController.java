package com.cabbooking.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/gateway")
public class GatewayTestController {

    @GetMapping("/health")
    public Mono<Map<String, String>> health() {
        return Mono.just(Map.of(
            "status", "UP",
            "service", "API-GATEWAY",
            "message", "Gateway is running successfully"
        ));
    }

    @GetMapping("/auth-test")
    public Mono<Map<String, String>> authTest() {
        return Mono.just(Map.of(
            "status", "AUTHENTICATED",
            "message", "You have successfully passed authentication"
        ));
    }
}
