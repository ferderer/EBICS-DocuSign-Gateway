package de.ferderer.ebicsdocusign.gateway.common.error;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GlobalExceptionHandlerIT extends IntegrationTestBase {

    @Test
    void shouldHandleBaseException() throws Exception {
        perform(post("/test/base-exception")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("E_NOT_FOUND"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.path").value("/test/base-exception"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.data.userId").value(123))
            .andExpect(jsonPath("$.data.resource").value("user"))
            .andExpect(jsonPath("$.invalid").doesNotExist());
    }

    @Test
    void shouldHandleGenericException() throws Exception {
        perform(post("/test/generic-exception")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value("Something went wrong"))
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            .andExpect(jsonPath("$.path").value("/test/generic-exception"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.invalid").doesNotExist());
    }

    @Test
    void shouldHandleValidationException() throws Exception {
        String invalidJson = """
            {
                "name": "",
                "email": "invalid-email"
            }
            """;

        perform(post("/test/validation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("E_INVALID_INPUT"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.path").value("/test/validation"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.invalid").isArray())
            .andExpect(jsonPath("$.invalid", hasSize(2)))
            .andExpect(jsonPath("$.invalid[*].path", containsInAnyOrder("name", "email")))
            .andExpect(jsonPath("$.invalid[*].code", containsInAnyOrder("Name is required", "Invalid email format")))
            .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldHandleConstraintViolationException() throws Exception {
        perform(post("/test/constraint-violation")
            .param("email", "invalid-email")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("E_INVALID_INPUT"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.path").value("/test/constraint-violation"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.invalid").isArray())
            .andExpect(jsonPath("$.invalid", hasSize(1)))
            .andExpect(jsonPath("$.invalid[0].path").value("testConstraintViolation.email"))
            .andExpect(jsonPath("$.invalid[0].code").exists())
            .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldHandleMalformedJson() throws Exception {
        String malformedJson = """
            {
                "name": "John",
                "email": "john@example.com"
                // missing closing brace
            """;

        perform(post("/test/validation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(malformedJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.path").value("/test/validation"))
            .andExpect(jsonPath("$.timestamp").exists());
    }
}
