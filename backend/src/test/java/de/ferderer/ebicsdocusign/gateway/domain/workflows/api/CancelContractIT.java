package de.ferderer.ebicsdocusign.gateway.domain.workflows.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.api.CancelContract.CancelContractRequest;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class CancelContractIT extends IntegrationTestBase {

    @Test
    void shouldCancelContractInProgress() throws Exception {
        var request = new CancelContractRequest("Client requested cancellation due to project scope change");
        
        perform(delete("/api/workflows/contracts/4003/cancel") // Contract in SENT status
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.contractId", is(4003)))
            .andExpect(jsonPath("$.paymentId", is(2004)))
            .andExpect(jsonPath("$.previousStatus", is("SENT")))
            .andExpect(jsonPath("$.cancellationReason", is("Client requested cancellation due to project scope change")))
            //.andExpect(jsonPath("$.docuSignResult", notNullValue()))
            .andExpect(jsonPath("$.cancelledAt", notNullValue()))
            ;
    }

    @Test
    void shouldCancelContractInCreatedStatus() throws Exception {
        var request = new CancelContractRequest("Payment verification failed - cancelling workflow");
        
        perform(delete("/api/workflows/contracts/4008/cancel") // Contract in CREATED status
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.previousStatus", is("CREATED")))
            .andExpect(jsonPath("$.cancellationReason", is("Payment verification failed - cancelling workflow")))
            //.andExpect(jsonPath("$.docuSignResult", is("No DocuSign envelope found to cancel")))
            ;
    }

    @Test
    void shouldCancelContractInErrorStatus() throws Exception {
        var request = new CancelContractRequest("Permanent error - unable to resolve, cancelling contract");
        
        perform(delete("/api/workflows/contracts/4005/cancel") // Contract in ERROR status
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.previousStatus", is("ERROR")))
            .andExpect(jsonPath("$.cancellationReason", is("Permanent error - unable to resolve, cancelling contract")));
    }

    @Test
    void shouldRejectCancellingCompletedContract() throws Exception {
        var request = new CancelContractRequest("Attempting to cancel completed contract");
        
        perform(delete("/api/workflows/contracts/4004/cancel") // Contract in COMPLETED status
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code", is("BUSINESS_RULE_VIOLATION")))
            .andExpect(jsonPath("$.data.currentStatus", is("COMPLETED")))
            .andExpect(jsonPath("$.data.reason", is("Cannot cancel completed contract")));
    }

    @Test
    void shouldRejectCancellingAlreadyCancelledContract() throws Exception {
        // First cancel a contract
        var request = new CancelContractRequest("First cancellation");
        perform(delete("/api/workflows/contracts/4001/cancel")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)));
        
        // Try to cancel again
        var secondRequest = new CancelContractRequest("Second cancellation attempt");
        perform(delete("/api/workflows/contracts/4001/cancel")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(secondRequest)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code", is("BUSINESS_RULE_VIOLATION")))
            .andExpect(jsonPath("$.data.reason", is("Contract is already cancelled")));
    }

    @Test
    void shouldReturnNotFoundForInvalidContract() throws Exception {
        var request = new CancelContractRequest("Test cancellation");
        
        perform(delete("/api/workflows/contracts/9999/cancel")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is("DATA_NOT_FOUND")))
            .andExpect(jsonPath("$.data.contractId", is(9999)));
    }
}
