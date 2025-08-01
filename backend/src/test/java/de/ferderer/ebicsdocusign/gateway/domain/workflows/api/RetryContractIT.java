package de.ferderer.ebicsdocusign.gateway.domain.workflows.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.api.RetryContract.RetryContractRequest;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class RetryContractIT extends IntegrationTestBase {

    @Test
    void shouldRetryContractInErrorStatus() throws Exception {
        var request = new RetryContractRequest("Fixing template mapping issue and retrying", null);
        
        perform(post("/api/workflows/contracts/4005/retry") // Contract in ERROR status
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.contractId", is(4005)))
            .andExpect(jsonPath("$.paymentId", is(2007)))
            .andExpect(jsonPath("$.previousStatus", is("ERROR")))
            .andExpect(jsonPath("$.newStatus", is("TEMPLATE_SELECTED"))) // Based on workflow state
            .andExpect(jsonPath("$.previousError", containsString("Template mapping failed")))
            .andExpect(jsonPath("$.retryReason", is("Fixing template mapping issue and retrying")))
            .andExpect(jsonPath("$.retriedAt", notNullValue()));
    }

    @Test
    void shouldRetryContractWithNewTemplate() throws Exception {
        var request = new RetryContractRequest("Using different template for retry", 3001L);
        
        perform(post("/api/workflows/contracts/4005/retry")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.newStatus", is("TEMPLATE_SELECTED")))
            .andExpect(jsonPath("$.retryReason", is("Using different template for retry")));
    }

    @Test
    void shouldRejectRetryForNonErrorStatus() throws Exception {
        var request = new RetryContractRequest("Attempting to retry completed contract", null);
        
        perform(post("/api/workflows/contracts/4004/retry") // Contract in COMPLETED status
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code", is("BUSINESS_RULE_VIOLATION")))
            .andExpect(jsonPath("$.data.currentStatus", is("COMPLETED")))
            .andExpect(jsonPath("$.data.reason", containsString("Can only retry contracts in ERROR status")));
    }

    @Test
    void shouldRejectRetryForInProgressContract() throws Exception {
        var request = new RetryContractRequest("Attempting to retry in-progress contract", null);
        
        perform(post("/api/workflows/contracts/4003/retry") // Contract in SENT status
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code", is("BUSINESS_RULE_VIOLATION")));
    }

    @Test
    void shouldReturnNotFoundForInvalidContract() throws Exception {
        var request = new RetryContractRequest("Test retry", null);
        
        perform(post("/api/workflows/contracts/9999/retry")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is("DATA_NOT_FOUND")))
            .andExpect(jsonPath("$.data.contractId", is(9999)));
    }
}
