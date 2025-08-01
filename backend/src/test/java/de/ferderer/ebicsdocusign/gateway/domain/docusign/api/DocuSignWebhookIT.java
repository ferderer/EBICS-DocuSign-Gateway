package de.ferderer.ebicsdocusign.gateway.domain.docusign.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.api.CreateEnvelope.CreateEnvelopeRequest;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.api.DocuSignWebhook.DocuSignWebhookRequest;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.EnvelopeCreated;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class DocuSignWebhookIT extends IntegrationTestBase {

    @Test
    void shouldProcessEnvelopeCompletedWebhook() throws Exception {
        // Create envelope first
        var createRequest = new CreateEnvelopeRequest(
            3001L,
            "client@acme-solutions.com",
            "John Smith",
            Map.of("amount", "15000.00"),
            2001L
        );
        
        var result = perform(post("/api/docusign/envelopes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(createRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String envelopeId = convertJson(result, EnvelopeCreated.class).envelopeId();
        
        // Send webhook
        var webhookRequest = new DocuSignWebhookRequest(
            "envelope-completed",
            envelopeId,
            "completed",
            "client@acme-solutions.com",
            LocalDateTime.now(),
            List.of()
        );
        
        perform(post("/api/docusign/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(webhookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Envelope status updated")))
                .andExpect(jsonPath("$.envelopeId", is(envelopeId)))
                .andExpect(jsonPath("$.currentStatus", is("COMPLETED")))
                .andExpect(jsonPath("$.processedAt", notNullValue()));
    }

    @Test
    void shouldProcessEnvelopeSignedWebhook() throws Exception {
        // Create envelope first
        var createRequest = new CreateEnvelopeRequest(
            3002L,
            "vendor@techpro.com",
            "Jane Doe",
            Map.of("amount", "8750.50"),
            null
        );
        
        var result = perform(post("/api/docusign/envelopes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(createRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String envelopeId = convertJson(result, EnvelopeCreated.class).envelopeId();
        
        // Send webhook  
        var webhookRequest = new DocuSignWebhookRequest(
            "envelope-signed",
            envelopeId,
            "signed",
            "vendor@techpro.com",
            LocalDateTime.now(),
            List.of()
        );
        
        perform(post("/api/docusign/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(webhookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus", is("SIGNED")));
    }

    @Test
    void shouldHandleNotFoundEnvelope() throws Exception {
        var webhookRequest = new DocuSignWebhookRequest(
            "envelope-completed",
            "non-existent-envelope",
            "completed",
            "test@example.com",
            LocalDateTime.now(),
            List.of()
        );
        
        perform(post("/api/docusign/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(webhookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Envelope not found")));
    }
}
