package com.onlinePharmacy.apigateway.filter;

import com.onlinePharmacy.apigateway.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            // Skip auth for public routes
            if (isPublicRoute(path, request.getMethod().name())) {
                return chain.filter(exchange);
            }

            // Extract token
            String token = extractToken(request);
            if (token == null) {
                return unauthorised(exchange, "Missing Authorization header");
            }

            // Validate token
            if (!jwtUtil.validateToken(token)) {
                return unauthorised(exchange, "Invalid or expired JWT token");
            }

            // Role-based route guard
            String role = jwtUtil.extractRole(token);
            if (!isAuthorised(path, role)) {
                return forbidden(exchange, "Access denied for role: " + role);
            }

            // Forward user context as headers to downstream services
            Long userId = jwtUtil.extractUserId(token);
            String email = jwtUtil.extractEmail(token);
            String name  = jwtUtil.extractName(token);

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id",    userId != null ? userId.toString() : "")
                    .header("X-User-Email", email != null  ? email : "")
                    .header("X-User-Role",  role != null   ? role  : "")
                    .header("X-User-Name",  name != null   ? name  : "")
                    .build();

            log.debug("Gateway: forwarding request [{}] {} | userId={} role={}",
                    request.getMethod(), path, userId, role);

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }

    // ── Route Visibility ──────────────────────────────────────────────────────

    private boolean isPublicRoute(String path, String method) {
        // Auth endpoints — always public
        if (path.startsWith("/api/auth/signup") || path.startsWith("/api/auth/login")) return true;

        // Catalog browsing — public GET only
        if (method.equalsIgnoreCase("GET")) {
            if (path.startsWith("/api/catalog/medicines")) return true;
            if (path.startsWith("/api/catalog/categories")) return true;
        }

        // Swagger / API docs — public
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) return true;

        return false;
    }

    // ── Role-Based Access ─────────────────────────────────────────────────────

    private boolean isAuthorised(String path, String role) {
        // Admin-only routes
        if (path.startsWith("/api/admin")) {
            return "ADMIN".equals(role);
        }
        // All authenticated users can access customer and catalog write routes
        return "CUSTOMER".equals(role) || "ADMIN".equals(role)
                || "PHARMACIST".equals(role) || "DELIVERY_AGENT".equals(role);
    }

    // ── Helper Responses ──────────────────────────────────────────────────────

    private Mono<Void> unauthorised(ServerWebExchange exchange, String message) {
        return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, message);
    }

    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        return writeErrorResponse(exchange, HttpStatus.FORBIDDEN, message);
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        String body = String.format(
                "{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status.value(), status.getReasonPhrase(), message);
        var buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    private String extractToken(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    public static class Config {
        // Config placeholder — extend for per-route config if needed
    }
}