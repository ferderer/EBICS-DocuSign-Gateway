package de.ferderer.ebicsdocusign.gateway.domain.ebics.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class LoadConnectionsIT extends IntegrationTestBase {

    @Test
    void shouldReturnAllEbicsConnections() throws Exception {
        perform(get("/api/ebics/connections"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(greaterThan(0))))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", notNullValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].bankName", notNullValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].hostId", notNullValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].partnerId", notNullValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].userId", notNullValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].bankUrl", notNullValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].version", notNullValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].status", notNullValue()));
    }

    @Test
    void shouldReturnConnectionsOrderedByCreationDate() throws Exception {
        perform(get("/api/ebics/connections"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(greaterThan(1))))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdAt", notNullValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].createdAt", notNullValue()));
    }

    @Test
    void shouldIncludeActiveConnections() throws Exception {
        perform(get("/api/ebics/connections"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.status == 'ACTIVE')]", hasSize(greaterThan(0))));
    }

    @Test
    void shouldIncludeConnectionWithLastConnectedTimestamp() throws Exception {
        perform(get("/api/ebics/connections"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.lastConnected != null)]", hasSize(greaterThan(0))));
    }
}
