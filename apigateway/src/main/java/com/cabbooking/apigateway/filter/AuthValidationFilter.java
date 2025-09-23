package com.cabbooking.apigateway.filter;

import com.cabbooking.apigateway.client.AuthServiceClient;
import com.cabbooking.apigateway.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthValidationFilter implements Filter {

    private final AuthServiceClient authServiceClient;
    private final ObjectMapper objectMapper;

    private static final List<String> PUBLIC_PATTERNS = List.of(
            "/api/auth/**",
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestPath = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        if (log.isDebugEnabled()) {
            log.debug("Incoming request method={} path={}", method, requestPath);
        }

        if ("OPTIONS".equalsIgnoreCase(method) || isPublicUrl(requestPath)) {
            if (log.isDebugEnabled()) {
                log.debug("Skipping auth for public/OPTIONS path={}", requestPath);
            }
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader)) {
            log.warn("Missing Authorization header for protected path={}", requestPath);
            sendError(httpResponse, HttpStatus.UNAUTHORIZED, "Missing Authorization header");
            return;
        }

        authHeader = authHeader.trim();
        if (!authHeader.startsWith("Bearer ")) {
            log.warn("Invalid Authorization format for path={} headerPreview={}...", requestPath,
                    authHeader.substring(0, Math.min(15, authHeader.length())));
            sendError(httpResponse, HttpStatus.UNAUTHORIZED, "Invalid Authorization header format. Expected: Bearer <token>");
            return;
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            log.warn("Empty token for path={}", requestPath);
            sendError(httpResponse, HttpStatus.UNAUTHORIZED, "Empty token in Authorization header");
            return;
        }

        try {
            Boolean isValid = authServiceClient.validateToken("Bearer " + token);
            if (Boolean.TRUE.equals(isValid)) {
                log.info("Token valid path={}", requestPath);
                chain.doFilter(request, response);
            } else {
                log.warn("Token invalid path={}", requestPath);
                sendError(httpResponse, HttpStatus.UNAUTHORIZED, "Invalid token");
            }
        } catch (Exception ex) {
            log.error("Exception during token validation path={} msg={}", requestPath, ex.getMessage());
            sendError(httpResponse, HttpStatus.UNAUTHORIZED, "Token validation failed");
        }
    }

    private boolean isPublicUrl(String requestPath) {
        for (String pattern : PUBLIC_PATTERNS) {
            if (PATH_MATCHER.match(pattern, requestPath)) {
                log.info("Public match pattern={} path={}", pattern, requestPath);
                return true;
            }
        }
        return false;
    }

    private void sendError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        ErrorResponse body = ErrorResponse.fail(message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}