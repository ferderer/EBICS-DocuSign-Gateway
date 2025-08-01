package de.ferderer.ebicsdocusign.gateway.domain.workflows.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class LoadContractsIT extends IntegrationTestBase {

    @Test
    void shouldLoadAllContracts() throws Exception {
        perform(get("/api/workflows/contracts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))))
            .andExpect(jsonPath("$[0].id", notNullValue()))
            .andExpect(jsonPath("$[0].paymentId", notNullValue()))
            .andExpect(jsonPath("$[0].transactionId", notNullValue()))
            .andExpect(jsonPath("$[0].status", notNullValue()))
            .andExpect(jsonPath("$[0].contractAmount", notNullValue()))
            .andExpect(jsonPath("$[0].clientName", notNullValue()))
            .andExpect(jsonPath("$[0].createdAt", notNullValue()));
    }

    @Test
    void shouldFilterContractsByStatus() throws Exception {
        perform(get("/api/workflows/contracts?status=COMPLETED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].status", everyItem(is("COMPLETED"))));
    }

    @Test
    void shouldRespectLimitParameter() throws Exception {
        perform(get("/api/workflows/contracts?limit=2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(lessThanOrEqualTo(2))));
    }

    @Test
    void shouldOrderByCreatedAtDesc() throws Exception {
        perform(get("/api/workflows/contracts?limit=5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))))
            .andExpect(jsonPath("$[0].createdAt", notNullValue()));
    }
}
