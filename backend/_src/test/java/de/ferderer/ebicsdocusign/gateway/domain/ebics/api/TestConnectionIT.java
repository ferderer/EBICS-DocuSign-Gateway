package de.ferderer.ebicsdocusign.gateway.domain.ebics.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.api.TestConnection.TestConnectionRequest;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class TestConnectionIT extends IntegrationTestBase {

@Test
void shouldTestConnectionSuccessfully() throws Exception {
    perform(post("/api/ebics/test-connection")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json(new TestConnectionRequest(1001L))))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.connectionId", is(1001)))
        .andExpect(jsonPath("$.bankName", is("Deutsche Bank AG")))
        .andExpect(jsonPath("$.hostId", is("DEUTDEFF")))
        .andExpect(jsonPath("$.success", is(true)))
        .andExpect(jsonPath("$.message", containsString("Connection successful")))
        .andExpect(jsonPath("$.testedAt", notNullValue()));
}

    @Test
    void shouldTestConnectionForInactiveConnection() throws Exception {
        perform(post("/api/ebics/test-connection")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(new TestConnectionRequest(1003L))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.connectionId", is(1003)))
            .andExpect(jsonPath("$.bankName", is("UniCredit Bank AG")))
            .andExpect(jsonPath("$.hostId", is("HYVEDEMM")))
            .andExpect(jsonPath("$.success", is(true)))  // Should succeed for INACTIVE status
            .andExpect(jsonPath("$.message", containsString("Connection successful")))
            .andExpect(jsonPath("$.testedAt", notNullValue()));
    }

    @Test
    void shouldFailTestConnectionForErrorStatus() throws Exception {
        perform(post("/api/ebics/test-connection")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(new TestConnectionRequest(1005L))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.connectionId", is(1005)))
            .andExpect(jsonPath("$.bankName", is("Landesbank Baden-Württemberg")))
            .andExpect(jsonPath("$.hostId", is("SOLADEST")))
            .andExpect(jsonPath("$.success", is(false)))
            .andExpect(jsonPath("$.message", containsString("Connection failed")))
            .andExpect(jsonPath("$.testedAt", notNullValue()));
    }

    @Test
    void shouldReturnNotFoundForInvalidConnectionId() throws Exception {
        perform(post("/api/ebics/test-connection")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(new TestConnectionRequest(9999L))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is("E_NOT_FOUND")))
            .andExpect(jsonPath("$.data.connectionId", is(9999)));
    }
}
