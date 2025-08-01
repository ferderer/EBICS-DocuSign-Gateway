package de.ferderer.ebicsdocusign.gateway.domain.payments.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class LoadIncomingPaymentsIT extends IntegrationTestBase {

    @Test
    void shouldLoadIncomingPayments() throws Exception {
        perform(get("/api/payments/incoming"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))))
            .andExpect(jsonPath("$[0].id", notNullValue()))
            .andExpect(jsonPath("$[0].transactionId", notNullValue()))
            .andExpect(jsonPath("$[0].receivedAt", notNullValue()))
            .andExpect(jsonPath("$[0].amount", notNullValue()))
            .andExpect(jsonPath("$[0].debtorName", notNullValue()))
            .andExpect(jsonPath("$[0].status", notNullValue()))
            .andExpect(jsonPath("$[0].bankName", notNullValue()));
    }

    @Test
    void shouldRespectLimitParameter() throws Exception {
        perform(get("/api/payments/incoming?limit=3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void shouldOrderByReceivedAtDesc() throws Exception {
        perform(get("/api/payments/incoming?limit=5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].transactionId", is("TXN-20240725-001")))
            .andExpect(jsonPath("$[1].transactionId", is("TXN-20240725-002")));
    }
}