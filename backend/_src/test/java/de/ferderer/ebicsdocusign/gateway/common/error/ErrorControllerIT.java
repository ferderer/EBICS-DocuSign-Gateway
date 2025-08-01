package de.ferderer.ebicsdocusign.gateway.common.error;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ErrorControllerIT {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldHandleNotFoundError() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/non-existent-endpoint",
            ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().error()).isEqualTo("Not Found");
        assertThat(response.getBody().path()).contains("/non-existent-endpoint");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void shouldHandleMethodNotAllowed() {
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/test/constraint-violation", // GET-only endpoint
            null,
            ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(405);
        assertThat(response.getBody().error()).isEqualTo("Method Not Allowed");
        assertThat(response.getBody().timestamp()).isNotNull();
    }
}
