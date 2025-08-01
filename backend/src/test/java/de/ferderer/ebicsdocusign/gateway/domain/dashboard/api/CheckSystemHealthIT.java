package de.ferderer.ebicsdocusign.gateway.domain.dashboard.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class CheckSystemHealthIT extends IntegrationTestBase {

    @Test
    void shouldReturnSystemHealthStatus() throws Exception {
        perform(get("/api/dashboard/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.overallStatus", in(new String[]{"HEALTHY", "DEGRADED", "UNHEALTHY"})))
            .andExpect(jsonPath("$.databaseHealthy", is(true)))
            .andExpect(jsonPath("$.activeEbicsConnections", greaterThanOrEqualTo(0)))
            .andExpect(jsonPath("$.totalEbicsConnections", greaterThan(0)))
            .andExpect(jsonPath("$.checkedAt", notNullValue()));
    }

    @Test
    void shouldReturnHealthyWhenEbicsConnectionsActive() throws Exception {
        perform(get("/api/dashboard/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ebicsHealthy", is(true)))
            .andExpect(jsonPath("$.overallStatus", is("HEALTHY")));
    }
}
