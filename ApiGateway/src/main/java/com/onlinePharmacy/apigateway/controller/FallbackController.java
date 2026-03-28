package com.onlinePharmacy.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, String>> authFallback() {
        return buildFallback("auth-service", "Authentication service is temporarily unavailable.");
    }

    @GetMapping("/catalog")
    public ResponseEntity<Map<String, String>> catalogFallback() {
        return buildFallback("catalog-service", "Catalog service is temporarily unavailable.");
    }

    @GetMapping("/orders")
    public ResponseEntity<Map<String, String>> ordersFallback() {
        return buildFallback("order-service", "Order service is temporarily unavailable.");
    }

    @GetMapping("/admin")
    public ResponseEntity<Map<String, String>> adminFallback() {
        return buildFallback("admin-service", "Admin service is temporarily unavailable.");
    }

    private ResponseEntity<Map<String, String>> buildFallback(String service, String message) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "status",  "503",
                "service", service,
                "message", message,
                "hint",    "Please try again in a few moments."
        ));
    }
}