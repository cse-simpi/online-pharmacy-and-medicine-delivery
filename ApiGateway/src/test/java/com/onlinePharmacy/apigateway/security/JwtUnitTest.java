package com.onlinePharmacy.apigateway.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    // Same secret used across all services (base64-encoded)
    private static final String SECRET =
            "dGhpcy1pcy1hLXZlcnktc2VjdXJlLXNlY3JldC1rZXktZm9yLXBoYXJtYWN5LWFwcA==";

    // A valid pre-generated token with role=CUSTOMER, userId=1
    // In real tests generate this via auth-service's JwtUtil
    private static final String VALID_TOKEN =
            "eyJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwicm9sZSI6IkNVU1RPTUVSIiwidXNlcklkIjoxLCJuYW1lIjoiVGVzdCBVc2VyIiwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjk5OTk5OTk5OTl9" +
            ".placeholder"; // Replace with real signed token in CI

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
    }

    @Test
    void validateToken_withInvalidToken_returnsFalse() {
        assertThat(jwtUtil.validateToken("not.a.jwt")).isFalse();
    }

    @Test
    void validateToken_withNullToken_returnsFalse() {
        assertThat(jwtUtil.validateToken(null)).isFalse();
    }

    @Test
    void validateToken_withEmptyToken_returnsFalse() {
        assertThat(jwtUtil.validateToken("")).isFalse();
    }

    @Test
    void validateToken_withExpiredToken_returnsFalse() {
        // An expired token (exp in the past)
        String expiredToken =
                "eyJhbGciOiJIUzI1NiJ9" +
                ".eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwicm9sZSI6IkNVU1RPTUVSIiwidXNlcklkIjoxLCJpYXQiOjE2MDAwMDAwMDAsImV4cCI6MTYwMDAwMDAwMX0" +
                ".someSignature";
        assertThat(jwtUtil.validateToken(expiredToken)).isFalse();
    }
}