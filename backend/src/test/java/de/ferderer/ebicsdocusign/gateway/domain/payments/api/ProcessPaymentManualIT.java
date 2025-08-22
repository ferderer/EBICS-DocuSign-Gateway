package de.ferderer.ebicsdocusign.gateway.domain.payments.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import de.ferderer.ebicsdocusign.gateway.domain.payments.api.ProcessPaymentManual.ProcessPaymentRequest;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class ProcessPaymentManualIT extends IntegrationTestBase {

    @Test
    void shouldProcessPaymentWithTemplate() throws Exception {
        var request = new ProcessPaymentRequest(
            3001L, // Service Contract Template
            "contracts@innovatetech.com",
            "InnovateTech Solutions GmbH",
            "Q3 2024 Innovation Project - AI Development and Consulting Services",
            "INNOV-CONTRACT-Q3-2024",
            false
        );
        
        perform(post("/api/payments/9001/process") // New test payment
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paymentId", is(9001)))
            .andExpect(jsonPath("$.transactionId", is("TEST-TXN-20240726-001")))
            .andExpect(jsonPath("$.amount", is("12500.00")))
            .andExpect(jsonPath("$.currency", is("EUR")))
            .andExpect(jsonPath("$.debtorName", is("InnovateTech Solutions")))
            .andExpect(jsonPath("$.workflowId", notNullValue()))
            .andExpect(jsonPath("$.workflowStatus", is("ENVELOPE_CREATED"))) // Advanced by processWorkflowSteps
            .andExpect(jsonPath("$.templateName", is("Service Contract Template")))
            .andExpect(jsonPath("$.clientEmail", is("contracts@innovatetech.com")))
            .andExpect(jsonPath("$.processedAt", notNullValue()));
    }

    @Test
    void shouldProcessPaymentWithoutTemplate() throws Exception {
        var request = new ProcessPaymentRequest(
            null,
            "security@securedata.com",
            "SecureData Systems GmbH",
            "Data Security Audit and Compliance Review Contract",
            "SEC-AUDIT-2024",
            false
        );
        
        perform(post("/api/payments/9002/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.workflowStatus", is("CREATED")))
            .andExpect(jsonPath("$.templateName", nullValue()));
    }

    @Test
    @Disabled
    void shouldSkipAmountValidation() throws Exception {
        var request = new ProcessPaymentRequest(
            3002L,
            "test@example.com",
            "Test Company",
            "Small contract below minimum",
            "TEST-001",
            true // Skip validation
        );
        
        // Use a small amount payment if available, or this should work with existing payments
        perform(post("/api/payments/9005/process") // 3450.75 EUR - above minimum anyway
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldRejectAlreadyProcessedPayment() throws Exception {
        var request = new ProcessPaymentRequest(
            3001L,
            "test@example.com",
            "Test Company",
            "Test contract",
            "TEST-001",
            false
        );
        
        perform(post("/api/payments/2001/process") // Payment already has workflow (4001)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code", is("BUSINESS_RULE_VIOLATION")))
            .andExpect(jsonPath("$.data.reason", containsString("Contract workflow already exists")));
    }

    @Test
    @Disabled
    void shouldRejectInvalidTemplate() throws Exception {
        var request = new ProcessPaymentRequest(
            9999L, // Invalid template ID
            "test@example.com",
            "Test Company",
            "Test contract",
            "TEST-001",
            false
        );
        
        perform(post("/api/payments/9003/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is("DATA_NOT_FOUND")))
            .andExpect(jsonPath("$.data.templateId", is(9999)));
    }

    @Test
    void shouldReturnNotFoundForInvalidPayment() throws Exception {
        var request = new ProcessPaymentRequest(
            3001L,
            "test@example.com",
            "Test Company",
            "Test contract",
            "TEST-001",
            false
        );
        
        perform(post("/api/payments/9999/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is("DATA_NOT_FOUND")))
            .andExpect(jsonPath("$.data.paymentId", is(9999)));
    }
}
