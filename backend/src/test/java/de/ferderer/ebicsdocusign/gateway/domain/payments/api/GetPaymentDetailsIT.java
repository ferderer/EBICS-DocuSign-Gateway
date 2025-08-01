package de.ferderer.ebicsdocusign.gateway.domain.payments.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class GetPaymentDetailsIT extends IntegrationTestBase {

    @Test
    void shouldGetPaymentDetails() throws Exception {
        perform(get("/api/payments/2001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(2001)))
            .andExpect(jsonPath("$.transactionId", is("TXN-20240725-001")))
            .andExpect(jsonPath("$.amount", is("15000.00")))
            .andExpect(jsonPath("$.currency", is("EUR")))
            .andExpect(jsonPath("$.debtorName", is("ACME Solutions GmbH")))
            .andExpect(jsonPath("$.status", is("RECEIVED")))
            .andExpect(jsonPath("$.connectionId", is(1001)))
            .andExpect(jsonPath("$.bankName", is("Deutsche Bank AG")))
            .andExpect(jsonPath("$.valueDate", notNullValue()))
            .andExpect(jsonPath("$.receivedAt", notNullValue()));
    }

    @Test
    void shouldReturnNotFoundForInvalidPaymentId() throws Exception {
        perform(get("/api/payments/9999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is("DATA_NOT_FOUND")))
            .andExpect(jsonPath("$.data.paymentId", is(9999)));
    }
}
