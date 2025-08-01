package de.ferderer.ebicsdocusign.gateway.domain.workflows.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class GetContractDetailsIT extends IntegrationTestBase {

    @Test
    void shouldGetCompleteContractDetails() throws Exception {
        perform(get("/api/workflows/contracts/4004")) // Completed contract
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(4004)))
            .andExpect(jsonPath("$.paymentId", is(2005)))
            .andExpect(jsonPath("$.transactionId", is("TXN-20240723-005")))
            .andExpect(jsonPath("$.status", is("COMPLETED")))
            .andExpect(jsonPath("$.contractAmount", is("31200.25")))
            .andExpect(jsonPath("$.contractCurrency", is("EUR")))
            .andExpect(jsonPath("$.clientName", is("Enterprise Solutions Ltd")))
            .andExpect(jsonPath("$.clientEmail", is("legal@enterprisesolutions.co.uk")))
            .andExpect(jsonPath("$.projectDescription", containsString("Annual Support Contract")))
            .andExpect(jsonPath("$.contractReference", is("ASC-2024-012")))
            .andExpect(jsonPath("$.templateId", is(3005)))
            .andExpect(jsonPath("$.templateName", is("Annual Support Agreement")))
            .andExpect(jsonPath("$.bankName", is("ING-DiBa AG")))
            .andExpect(jsonPath("$.createdAt", notNullValue()))
            .andExpect(jsonPath("$.completedAt", notNullValue()));
    }

    @Test
    void shouldGetContractWithError() throws Exception {
        perform(get("/api/workflows/contracts/4005")) // Error contract
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(4005)))
            .andExpect(jsonPath("$.status", is("ERROR")))
            .andExpect(jsonPath("$.errorMessage", containsString("Template mapping failed")))
            .andExpect(jsonPath("$.completedAt", nullValue()));
    }

    @Test
    void shouldGetContractInProgress() throws Exception {
        perform(get("/api/workflows/contracts/4001")) // Template selected
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(4001)))
            .andExpect(jsonPath("$.status", is("TEMPLATE_SELECTED")))
            .andExpect(jsonPath("$.envelopeId_external", nullValue()))
            .andExpect(jsonPath("$.sentAt", nullValue()))
            .andExpect(jsonPath("$.completedAt", nullValue()));
    }

    @Test
    void shouldReturnNotFoundForInvalidContract() throws Exception {
        perform(get("/api/workflows/contracts/9999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is("DATA_NOT_FOUND")))
            .andExpect(jsonPath("$.data.contractId", is(9999)));
    }
}
