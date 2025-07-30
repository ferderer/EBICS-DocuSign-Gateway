package de.ferderer.ebicsdocusign.gateway.common.test;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    public static class TestRequest {
        @NotNull(message = "Name cannot be null")
        @NotBlank(message = "Name cannot be blank")
        private String name;

        @NotNull(message = "Email cannot be null")
        private String email;

        // Constructors, getters, setters
        public TestRequest() {}

        public TestRequest(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @GetMapping("/app-exception")
    public String throwAppException() {
        throw new AppException(ErrorCode.BUSINESS_RULE_VIOLATION)
            .withData("userId", 123)
            .withData("operation", "transfer");
    }

    @GetMapping("/app-exception-with-cause")
    public String throwAppExceptionWithCause() {
        RuntimeException cause = new RuntimeException("Root cause");
        throw new AppException(ErrorCode.DATA_NOT_FOUND, cause);
    }

    @GetMapping("/security-exception")
    public String throwSecurityException() {
        throw new AccessDeniedException("Access denied to resource");
    }

    @GetMapping("/dao-exception")
    public String throwDaoException() {
        throw new DuplicateKeyException("Duplicate key violation");
    }

    @GetMapping("/generic-exception")
    public String throwGenericException() {
        throw new IllegalArgumentException("Invalid argument provided");
    }

    @PostMapping("/validation-exception")
    public TestRequest throwValidationException(@Valid @RequestBody TestRequest request) {
        return request;
    }

    @GetMapping("/runtime-exception")
    public String throwRuntimeException() {
        throw new RuntimeException("Unexpected error");
    }
}
