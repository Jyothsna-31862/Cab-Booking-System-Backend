package com.cabbooking.apigateway.client;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthServiceClientFallback implements AuthServiceClient {
    @Override
    public Boolean validateToken(String token) {
        log.error("[CIRCUIT BREAKER] AuthService is unavailable. Fallback for validateToken invoked.");
        return false;
    }
}

