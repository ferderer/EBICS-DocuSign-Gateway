package de.ferderer.ebicsdocusign.gateway.common.error;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ErrorResponseBuilderTest {

    @Test
    void shouldBuildResponseForBaseException() {
        AppException exception = new AppException(ErrorCode.E_NOT_FOUND, HttpStatus.NOT_FOUND, "userId", 123);

        ErrorResponse response = new ErrorResponseBuilder().buildErrorResponse(exception, "/api/users/123");

        assertThat(response.status()).isEqualTo(404);
        assertThat(response.error()).isEqualTo("Not Found");
        assertThat(response.code()).isEqualTo("E_NOT_FOUND");
        assertThat(response.path()).isEqualTo("/api/users/123");
        assertThat(response.data()).containsEntry("userId", 123);
        assertThat(response.invalid()).isNull();
        assertThat(response.timestamp()).isNotNull();
    }

    @Test
    void shouldBuildResponseForGenericException() {
        RuntimeException exception = new RuntimeException("Unexpected error");

        ErrorResponse response = new ErrorResponseBuilder().buildErrorResponse(exception, "/api/test");

        assertThat(response.status()).isEqualTo(500);
        assertThat(response.error()).isEqualTo("Internal Server Error");
        assertThat(response.code()).isEqualTo("Unexpected error");
        assertThat(response.path()).isEqualTo("/api/test");
        assertThat(response.data()).isNull();
        assertThat(response.invalid()).isNull();
        assertThat(response.timestamp()).isNotNull();
    }

    @Test
    void shouldMapKnownExceptionToCorrectStatus() {
        org.springframework.security.authentication.BadCredentialsException exception =
            new org.springframework.security.authentication.BadCredentialsException("Bad credentials");

        ErrorResponse response = new ErrorResponseBuilder().buildErrorResponse(exception, "/api/login");

        assertThat(response.status()).isEqualTo(401);
        assertThat(response.error()).isEqualTo("Unauthorized");
    }
}
