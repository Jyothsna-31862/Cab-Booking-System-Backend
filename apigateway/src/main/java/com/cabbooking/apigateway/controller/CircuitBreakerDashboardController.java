package com.cabbooking.apigateway.controller;

import com.cabbooking.apigateway.service.CircuitBreakerMonitoringService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class CircuitBreakerDashboardController {

    private final CircuitBreakerMonitoringService monitoringService;

    @GetMapping("/circuit-breakers")
    public String circuitBreakerDashboard(Model model) {
        Map<String, CircuitBreaker.State> states = monitoringService.getAllCircuitBreakerStates();

        Map<String, String> serviceStatus = states.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> getStatusColor(entry.getValue())
            ));

        model.addAttribute("circuitBreakerStates", states);
        model.addAttribute("serviceStatus", serviceStatus);
        model.addAttribute("openServices", monitoringService.getOpenCircuitBreakers());
        model.addAttribute("halfOpenServices", monitoringService.getHalfOpenCircuitBreakers());

        return "circuit-breaker-dashboard";
    }

    private String getStatusColor(CircuitBreaker.State state) {
        switch (state) {
            case CLOSED:
                return "success";
            case OPEN:
                return "danger";
            case HALF_OPEN:
                return "warning";
            default:
                return "secondary";
        }
    }
}
