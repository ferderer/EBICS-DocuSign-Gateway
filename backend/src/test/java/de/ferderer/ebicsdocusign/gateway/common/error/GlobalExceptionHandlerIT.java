package de.ferderer.ebicsdocusign.gateway.common.error;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import de.ferderer.ebicsdocusign.gateway.common.test.TestController.TestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GlobalExceptionHandlerIT extends IntegrationTestBase {
    @Test
    void handleAppException_shouldReturnCorrectStatusAndBody() throws Exception {
        perform(get("/test/app-exception"))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"))
            .andExpect(jsonPath("$.message").value("Business rule validation failed"))
            .andExpect(jsonPath("$.data.userId").value(123))
            .andExpect(jsonPath("$.data.operation").value("transfer"));
    }

    @Test
    void handleAppExceptionWithCause_shouldReturnCorrectResponse() throws Exception {
        perform(get("/test/app-exception-with-cause"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("DATA_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Requested resource not found"))
            .andExpect(jsonPath("$.data").exists());
    }

    // Tests for various exception types

    @Test
    void handleSecurityException_shouldReturnForbidden() throws Exception {
        perform(get("/test/security-exception"))
            .andExpect(status().isForbidden())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("AUTH_ACCESS_DENIED"))
            .andExpect(jsonPath("$.message").value("Access denied to requested resource"));
    }

    @Test
    void handleDaoException_shouldReturnConflict() throws Exception {
        perform(get("/test/dao-exception"))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("DATA_DUPLICATE_RESOURCE"))
            .andExpect(jsonPath("$.message").value("Resource already exists"));
    }

    @Test
    void handleGenericException_shouldReturnBadRequest() throws Exception {
        perform(get("/test/generic-exception"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("VALIDATION_INVALID_ARGUMENT"))
            .andExpect(jsonPath("$.message").value("Invalid argument provided"));
    }

    @Test
    void handleRuntimeException_shouldReturnInternalServerError() throws Exception {
        perform(get("/test/runtime-exception"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
            .andExpect(jsonPath("$.message").value("Internal server error occurred"));
    }

    @Test
    void handleValidationException_withInvalidJson_shouldReturnBadRequest() throws Exception {
        perform(post("/test/validation-exception")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("VALIDATION_INVALID_JSON"))
            .andExpect(jsonPath("$.message").value("Invalid JSON format in request body"));
    }

    @Test
    void handleValidationException_withValidationErrors_shouldIncludeInvalidFields() throws Exception {
        perform(post("/test/validation-exception")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new TestRequest(null, null))))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("VALIDATION_INVALID_INPUT"))
            .andExpect(jsonPath("$.message").value("Input validation failed"))
            .andExpect(jsonPath("$.invalid").isArray())
            .andExpect(jsonPath("$.invalid.length()").value(3));
    }

    @Test
    void handleValidationException_withBlankName_shouldIncludeFieldDetails() throws Exception {
        perform(post("/test/validation-exception")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new TestRequest("", "test@example.com"))))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("VALIDATION_INVALID_INPUT"))
            .andExpect(jsonPath("$.invalid").isArray())
            .andExpect(jsonPath("$.invalid[0].path").value("name"))
            .andExpect(jsonPath("$.invalid[0].code").value("Name cannot be blank"))
            .andExpect(jsonPath("$.invalid[0].value").value(""));
    }

    @Test
    void allExceptions_shouldHaveConsistentResponseFormat() throws Exception {
        // Test that all error responses have the required fields
        String[] endpoints = {
            "/test/app-exception",
            "/test/security-exception", 
            "/test/dao-exception",
            "/test/generic-exception",
            "/test/runtime-exception"
        };

        for (String endpoint : endpoints) {
            perform(get(endpoint))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    @Test
    void errorResponses_shouldNotExposeInternalDetails() throws Exception {
        // Ensure error responses don't leak sensitive information
        perform(get("/test/runtime-exception"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("Internal server error occurred"))
            .andExpect(jsonPath("$.exception").doesNotExist())
            .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void httpMethodNotSupported_shouldReturnMethodNotAllowed() throws Exception {
        perform(delete("/test/app-exception"))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("VALIDATION_METHOD_NOT_ALLOWED"))
            .andExpect(jsonPath("$.message").value("HTTP method not allowed for this endpoint"));
    }

    @Test
    void missingRequestBody_shouldReturnBadRequest() throws Exception {
        perform(post("/test/validation-exception")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("VALIDATION_INVALID_JSON"));
    }
}
