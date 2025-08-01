package de.ferderer.ebicsdocusign.gateway.domain.workflows.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.api.CreateContract.CreateContractRequest;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class CreateContractIT extends IntegrationTestBase {

    @Test
    void shouldCreateContractFromPayment() throws Exception {
        var request = new CreateContractRequest(
            9001L, // Marketing Plus GmbH - should not have workflow yet
            "contracts@marketingplus.de",
            "Marketing Plus GmbH", 
            "Digital Marketing Contract Q3 2024",
            "MKT-CONTRACT-2024-Q3",
            3004L
        );
        
        perform(post("/api/workflows/contracts/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.paymentId", is(9001)))
            .andExpect(jsonPath("$.transactionId", is("TEST-TXN-20240726-001")))
            .andExpect(jsonPath("$.status", is("TEMPLATE_SELECTED")))
            .andExpect(jsonPath("$.contractAmount", is("12500.00")))
            .andExpect(jsonPath("$.contractCurrency", is("EUR")))
            .andExpect(jsonPath("$.clientName", is("Marketing Plus GmbH")))
            .andExpect(jsonPath("$.clientEmail", is("contracts@marketingplus.de")))
            .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    void shouldCreateContractWithoutTemplate() throws Exception {
        var request = new CreateContractRequest(
            9002L, // Legal Advisory Partners - use different payment
            "contracts@legaladvisory.com",
            "Legal Advisory Partners GmbH",
            "Legal Services Contract",
            "LEG-CONTRACT-2024",
            null
        );
        
        perform(post("/api/workflows/contracts/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("CREATED")));
    }

    @Test
    void shouldReturnNotFoundForInvalidPayment() throws Exception {
        var request = new CreateContractRequest(
            9999L,
            "test@example.com",
            "Test Company",
            "Test contract",
            "TEST-001",
            null
        );
        
        perform(post("/api/workflows/contracts/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is("DATA_NOT_FOUND")))
            .andExpect(jsonPath("$.data.paymentId", is(9999)));
    }

    @Test
    void shouldPreventDuplicateWorkflowForSamePayment() throws Exception {
        var request = new CreateContractRequest(
            2001L, // Payment that already has workflow (4001)
            "new-email@acme-solutions.com",
            "ACME Solutions GmbH",
            "Duplicate contract attempt",
            "DUP-001",
            null
        );
        
        perform(post("/api/workflows/contracts/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code", is("BUSINESS_RULE_VIOLATION")));
    }
}
