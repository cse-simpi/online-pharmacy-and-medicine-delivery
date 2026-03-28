package com.onlinePharmacy.apigateway.config;


import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
public class RateLimiterConfig {

    /**
     * Rate-limit key based on the client's remote IP address.
     * Used by Spring Cloud Gateway's RequestRateLimiter filter via Redis.
     * Referenced in application.yml as: key-resolver: "#{@ipKeyResolver}"
     */
	@Primary
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
                Objects.requireNonNull(
                        exchange.getRequest().getRemoteAddress(),
                        "Remote address is null"
                ).getAddress().getHostAddress()
        );
    }

    /**
     * User-based rate limiter key — extracted from X-User-Id header
     * forwarded by JwtAuthFilter. Use this for per-user limiting
     * by swapping the key-resolver in application.yml.
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            return Mono.just(userId != null ? userId : "anonymous");
        };
    }
}