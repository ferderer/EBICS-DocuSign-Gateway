package de.ferderer.ebicsdocusign.gateway.domain.docusign.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.api.CreateEnvelope.CreateEnvelopeRequest;
import java.util.Map;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class CreateEnvelopeIT extends IntegrationTestBase {

    @Test
    void shouldCreateEnvelopeFromTemplate() throws Exception {
        var request = new CreateEnvelopeRequest(3001L, "client@acme-solutions.com", "John Smith",
            Map.of("amount", "15000.00", "debtorName", "ACME Solutions GmbH"), 2001L);
        
        perform(post("/api/docusign/envelopes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.envelopeId", startsWith("envelope-")))
            .andExpect(jsonPath("$.templateName", is("Service Contract Template")))
            .andExpect(jsonPath("$.signerEmail", is("client@acme-solutions.com")))
            .andExpect(jsonPath("$.signerName", is("John Smith")))
            .andExpect(jsonPath("$.status", is("SENT")))
            .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    void shouldCreateEnvelopeWithoutPaymentId() throws Exception {
        var request = new CreateEnvelopeRequest(3002L, "vendor@techpro.com", "Jane Doe", Map.of("amount", "8750.50"), null);
        
        perform(post("/api/docusign/envelopes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.templateName", is("Purchase Order Agreement")));
    }

    @Test
    void shouldReturnNotFoundForInvalidTemplate() throws Exception {
        var request = new CreateEnvelopeRequest(9999L, "test@example.com", "Test User", Map.of(), null);
        
        perform(post("/api/docusign/envelopes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is("DATA_NOT_FOUND")))
            .andExpect(jsonPath("$.data.templateId", is(9999)));
    }

    @Test
    void shouldValidateRequiredFields() throws Exception {
        var request = new CreateEnvelopeRequest(3001L, "invalid-email", "", Map.of(), null);
        
        perform(post("/api/docusign/envelopes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(request)))
            .andExpect(status().isBadRequest());
    }
}
