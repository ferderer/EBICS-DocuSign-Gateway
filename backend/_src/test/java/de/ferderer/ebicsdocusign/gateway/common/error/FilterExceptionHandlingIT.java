package de.ferderer.ebicsdocusign.gateway.common.error;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilterExceptionHandlingIT {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @TestConfiguration
    static class FilterConfig {
        @Bean
        public FilterRegistrationBean<ExceptionThrowingFilter> exceptionFilter() {
            FilterRegistrationBean<ExceptionThrowingFilter> registration = new FilterRegistrationBean<>();
            registration.setFilter(new ExceptionThrowingFilter());
            registration.addUrlPatterns("/filter-error/*");
            registration.setOrder(1);
            return registration;
        }
    }

    static class ExceptionThrowingFilter implements Filter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            if (httpRequest.getRequestURI().contains("/filter-error/exception")) {
                throw new RuntimeException("Filter exception");
            }
            chain.doFilter(request, response);
        }
    }

    @Test
    void shouldHandleFilterException() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/filter-error/exception",
            ErrorResponse.class
        );

        // Filter exceptions should still be caught by our ErrorController
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().error()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().code()).isEqualTo("Filter exception");
        assertThat(response.getBody().timestamp()).isNotNull();
    }
}
