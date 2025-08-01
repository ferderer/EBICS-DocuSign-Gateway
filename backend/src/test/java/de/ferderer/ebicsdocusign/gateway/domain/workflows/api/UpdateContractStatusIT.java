package de.ferderer.ebicsdocusign.gateway.domain.workflows.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.api.UpdateContractStatus.UpdateContractStatusRequest;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class UpdateContractStatusIT extends IntegrationTestBase {

    @Test
    void shouldUpdateContractStatusFromCreatedToTemplateSelected() throws Exception {
        var request = new UpdateContractStatusRequest(
            "TEMPLATE_SELECTED",
            "Template manually selected by operator",
            null
        );
        
        perform(put("/api/workflows/contracts/4008/status") // Contract in CREATED status
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.contractId", is(4008)))
            .andExpect(jsonPath("$.paymentId", is(2006)))
            .andExpect(jsonPath("$.previousStatus", is("CREATED")))
            .andExpect(jsonPath("$.newStatus", is("TEMPLATE_SELECTED")))
            .andExpect(jsonPath("$.reason", is("Template manually selected by operator")))
            .andExpect(jsonPath("$.updatedAt", notNullValue()));
    }

    @Test
    void shouldUpdateContractStatusToError() throws Exception {
        var request = new UpdateContractStatusRequest(
            "ERROR",
            "Failed to create envelope",
            "DocuSign API authentication failed - invalid credentials"
        );
        
        perform(put("/api/workflows/contracts/4001/status") // Contract in TEMPLATE_SELECTED status
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.previousStatus", is("TEMPLATE_SELECTED")))
            .andExpect(jsonPath("$.newStatus", is("ERROR")));
    }

    @Test
    void shouldUpdateContractStatusToCompleted() throws Exception {
        var request = new UpdateContractStatusRequest("COMPLETED", "Contract manually marked as completed", null);
        
        perform(put("/api/workflows/contracts/4006/status") // Contract in SIGNED status
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.previousStatus", is("SIGNED")))
            .andExpect(jsonPath("$.newStatus", is("COMPLETED")));
    }

    @Test
    void shouldRejectInvalidStatusTransition() throws Exception {
        var request = new UpdateContractStatusRequest("SENT", "Invalid transition attempt", null);
        
        perform(put("/api/workflows/contracts/4008/status") // Contract in CREATED status
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code", is("BUSINESS_RULE_VIOLATION")))
            .andExpect(jsonPath("$.data.currentStatus", is("CREATED")))
            .andExpect(jsonPath("$.data.targetStatus", is("SENT")));
    }

    @Test
    void shouldRejectInvalidStatusValue() throws Exception {
        var request = new UpdateContractStatusRequest("INVALID_STATUS", "Testing invalid status", null);
        
        perform(put("/api/workflows/contracts/4001/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code", is("VALIDATION_INVALID_INPUT")));
    }

    @Test
    void shouldReturnNotFoundForInvalidContract() throws Exception {
        var request = new UpdateContractStatusRequest("ERROR", "Test", null);
        
        perform(put("/api/workflows/contracts/9999/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is("DATA_NOT_FOUND")));
    }
}
