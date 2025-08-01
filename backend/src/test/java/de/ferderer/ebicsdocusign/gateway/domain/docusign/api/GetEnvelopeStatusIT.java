package de.ferderer.ebicsdocusign.gateway.domain.docusign.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.api.CreateEnvelope.CreateEnvelopeRequest;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.EnvelopeStatusInfo;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
class GetEnvelopeStatusIT extends IntegrationTestBase {

    @Test
    void shouldGetEnvelopeStatus() throws Exception {
        var createRequest = new CreateEnvelopeRequest(3001L, "client@acme-solutions.com", "John Smith", Map.of("amount", "15000.00"), 2001L);
        
        var result = perform(post("/api/docusign/envelopes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(createRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        log.info("Read response body: {}", result);
        
        EnvelopeStatusInfo envelope = convertJson(result, EnvelopeStatusInfo.class);
        String envelopeId = envelope.envelopeId();
        log.info("Extract envelopeID: {}", envelopeId);
        
        // Now get the envelope status
        perform(get("/api/docusign/envelopes/{envelopeId}/status", envelopeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.envelopeId", is(envelopeId)))
            .andExpect(jsonPath("$.templateName", is("Service Contract Template")))
            .andExpect(jsonPath("$.signerEmail", is("client@acme-solutions.com")))
            .andExpect(jsonPath("$.signerName", is("John Smith")))
            .andExpect(jsonPath("$.status", is("SENT")))
            .andExpect(jsonPath("$.paymentId", is(2001)))
            .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    void shouldReturnNotFoundForInvalidEnvelopeId() throws Exception {
        perform(get("/api/docusign/envelopes/invalid-envelope-id/status"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is("DATA_NOT_FOUND")))
            .andExpect(jsonPath("$.data.envelopeId", is("invalid-envelope-id")));
    }
}
