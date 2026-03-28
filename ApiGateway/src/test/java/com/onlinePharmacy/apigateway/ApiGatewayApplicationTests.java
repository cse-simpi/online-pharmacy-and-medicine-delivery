package com.onlinePharmacy.apigateway;

import com.onlinePharmacy.apigateway.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiGatewayApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void contextLoads() {
        // Verifies the Spring context starts without errors
    }

    @Test
    void publicRoute_signup_doesNotRequireToken() {
        // Check that the gateway doesn't return 401/403 for a public route
        webTestClient.post()
                .uri("/api/auth/signup")
                .exchange()
                // Option A: Assert it's NOT these specific statuses using value()
                .expectStatus().value(status -> {
                    if (status == 401 || status == 403) {
                        throw new AssertionError("Route should be public but returned " + status);
                    }
                });
    }

    @Test
    void protectedRoute_withoutToken_returns401() {
        webTestClient.get()
                .uri("/api/orders/cart")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void protectedRoute_withInvalidToken_returns401() {
        when(jwtUtil.validateToken("bad-token")).thenReturn(false);

        webTestClient.get()
                .uri("/api/orders/cart")
                .header("Authorization", "Bearer bad-token")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void adminRoute_withCustomerRole_returns403() {
        String fakeToken = "valid-customer-token";
        when(jwtUtil.validateToken(fakeToken)).thenReturn(true);
        when(jwtUtil.extractRole(fakeToken)).thenReturn("CUSTOMER");
        when(jwtUtil.extractEmail(fakeToken)).thenReturn("customer@test.com");
        when(jwtUtil.extractUserId(fakeToken)).thenReturn(1L);
        when(jwtUtil.extractName(fakeToken)).thenReturn("Test Customer");

        webTestClient.get()
                .uri("/api/admin/dashboard")
                .header("Authorization", "Bearer " + fakeToken)
                .exchange()
                .expectStatus().isForbidden();
    }
}