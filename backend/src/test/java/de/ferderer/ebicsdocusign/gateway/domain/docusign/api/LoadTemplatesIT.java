package de.ferderer.ebicsdocusign.gateway.domain.docusign.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class LoadTemplatesIT extends IntegrationTestBase {

    @Test
    void shouldReturnActiveTemplates() throws Exception {
        perform(get("/api/docusign/templates"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(6)))  // Only ACTIVE templates
            .andExpect(jsonPath("$[0].templateId", notNullValue()))
            .andExpect(jsonPath("$[0].templateName", notNullValue()))
            .andExpect(jsonPath("$[0].status", is("ACTIVE")))
            .andExpect(jsonPath("$[0].documentName", notNullValue()));
    }

    @Test
    void shouldOrderTemplatesByName() throws Exception {
        perform(get("/api/docusign/templates"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].templateName", is("Annual Support Agreement")))
            .andExpect(jsonPath("$[1].templateName", is("Legal Services Retainer")));
    }

    @Test
    void shouldNotIncludeInactiveOrDraftTemplates() throws Exception {
        perform(get("/api/docusign/templates"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.status == 'INACTIVE')]", hasSize(0)))
            .andExpect(jsonPath("$[?(@.status == 'DRAFT')]", hasSize(0)));
    }
}