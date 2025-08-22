package de.ferderer.ebicsdocusign.gateway.domain.ebics.api;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.api.DownloadStatements.DownloadStatementsRequest;
import java.time.LocalDate;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class DownloadStatementsIT extends IntegrationTestBase {

    @Test
    void shouldDownloadStatementsWithDateRange() throws Exception {
        var request = new DownloadStatementsRequest(1001L, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        
        perform(post("/api/ebics/download-statements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.connectionId", is(1001)))
                .andExpect(jsonPath("$.bankName", is("Deutsche Bank AG")))
                .andExpect(jsonPath("$.fromDate", is("2024-01-01")))
                .andExpect(jsonPath("$.toDate", is("2024-01-31")))
                .andExpect(jsonPath("$.statementCount", greaterThan(0)))
                .andExpect(jsonPath("$.statements", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.downloadedAt", notNullValue()));
    }

    @Test
    void shouldDownloadStatementsWithDefaultDateRange() throws Exception {
        var request = new DownloadStatementsRequest(1001L, null, null);
        
        perform(post("/api/ebics/download-statements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statementCount", greaterThan(0)))
                .andExpect(jsonPath("$.fromDate", notNullValue()))
                .andExpect(jsonPath("$.toDate", notNullValue()));
    }

    @Test
    void shouldReturnNotFoundForInvalidConnection() throws Exception {
        var request = new DownloadStatementsRequest(9999L, null, null);
        
        perform(post("/api/ebics/download-statements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("DATA_NOT_FOUND")));
    }
}
