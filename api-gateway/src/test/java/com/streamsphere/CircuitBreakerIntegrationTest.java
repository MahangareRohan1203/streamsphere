package com.streamsphere;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
@AutoConfigureWebTestClient
class CircuitBreakerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void authServiceFallbackTest() {
        // Calling /auth/login when auth-service is not available should trigger fallback
        webTestClient.post().uri("/auth/login")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").value(equalTo("SERVICE_UNAVAILABLE"))
                .jsonPath("$.message").value(equalTo("Authentication service is currently unavailable. Please try again later."));
    }

    @Test
    void videoServiceFallbackTest() {
        // Calling /api/videos when video-service is not available should trigger fallback
        webTestClient.get().uri("/api/videos")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").value(equalTo("SERVICE_UNAVAILABLE"))
                .jsonPath("$.message").value(equalTo("Video service is currently unavailable. Some features may be restricted."));
    }
}
