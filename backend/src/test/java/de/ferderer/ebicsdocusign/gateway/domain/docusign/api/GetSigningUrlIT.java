package de.ferderer.ebicsdocusign.gateway.domain.docusign.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.api.CreateEnvelope.CreateEnvelopeRequest;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.SigningUrlResponse;
import java.util.Map;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class GetSigningUrlIT extends IntegrationTestBase {

    @Test
    void shouldGenerateSigningUrl() throws Exception {
        // First create an envelope
        var createRequest = new CreateEnvelopeRequest(3001L, "client@acme-solutions.com", "John Smith", Map.of("amount", "15000.00"), 2001L);
        
        var result = perform(post("/api/docusign/envelopes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(createRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        String envelopeId = convertJson(result, SigningUrlResponse.class).envelopeId();
        
        // Generate signing URL
        perform(get("/api/docusign/envelopes/{envelopeId}/signing-url", envelopeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.envelopeId", is(envelopeId)))
            .andExpect(jsonPath("$.signingUrl", startsWith("https://demo.docusign.net")))
            .andExpect(jsonPath("$.signerEmail", is("client@acme-solutions.com")))
            .andExpect(jsonPath("$.signerName", is("John Smith")))
            .andExpect(jsonPath("$.returnUrl", is("http://localhost:4200/signing/complete")))
            .andExpect(jsonPath("$.expiresAt", notNullValue()))
            .andExpect(jsonPath("$.generatedAt", notNullValue()));
    }

    @Test
    void shouldUseCustomReturnUrl() throws Exception {
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
        
        String envelopeId = convertJson(result, SigningUrlResponse.class).envelopeId();
        
        // Generate signing URL with custom return URL
        perform(get("/api/docusign/envelopes/{envelopeId}/signing-url?returnUrl=https://myapp.com/success", envelopeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.returnUrl", is("https://myapp.com/success")));
    }

    @Test
    void shouldReturnNotFoundForInvalidEnvelope() throws Exception {
        perform(get("/api/docusign/envelopes/invalid-envelope/signing-url"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is("DATA_NOT_FOUND")))
            .andExpect(jsonPath("$.data.envelopeId", is("invalid-envelope")));
    }
}
