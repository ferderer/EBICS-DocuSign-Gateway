package de.ferderer.ebicsdocusign.gateway.domain.ebics.api;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.EbicsConnectionEntity;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.EbicsConnectionRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DownloadStatements {
    private final EbicsConnectionRepository repository;

    public record DownloadStatementsRequest(
        @NotNull Long connectionId,
        LocalDate fromDate,
        LocalDate toDate
    ) {}

    public record StatementEntry(
        String transactionId,
        LocalDate valueDate,
        LocalDate bookingDate,
        String amount,
        String currency,
        String debtorName,
        String debtorAccount,
        String creditorName,
        String creditorAccount,
        String remittanceInfo,
        String endToEndId
    ) {}

    public record DownloadStatementsResponse(
        Long connectionId,
        String bankName,
        String hostId,
        LocalDate fromDate,
        LocalDate toDate,
        int statementCount,
        List<StatementEntry> statements,
        LocalDateTime downloadedAt
    ) {}

    @PostMapping("/api/ebics/download-statements")
    public DownloadStatementsResponse downloadStatements(@Valid @RequestBody DownloadStatementsRequest request) {
        var connection = repository.findById(request.connectionId())
            .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND)
                .withData("connectionId", request.connectionId()));

        LocalDateTime downloadedAt = LocalDateTime.now();
        LocalDate fromDate = request.fromDate() != null ? request.fromDate() : LocalDate.now().minusDays(30);
        LocalDate toDate = request.toDate() != null ? request.toDate() : LocalDate.now();

        try {
            // TODO: Implement actual EBICS statement download
            List<StatementEntry> statements = downloadStatement(connection, fromDate, toDate);
            
            return new DownloadStatementsResponse(
                connection.getId(),
                connection.getBankName(),
                connection.getHostId(),
                fromDate,
                toDate,
                statements.size(),
                statements,
                downloadedAt
            );
        }
        catch (Exception e) {
            throw new AppException(ErrorCode.EBICS_CONNECTION_FAILED)
                .withData("connectionId", request.connectionId())
                .withData("error", e.getMessage());
        }
    }

    private List<StatementEntry> downloadStatement(EbicsConnectionEntity connection, LocalDate fromDate, LocalDate toDate) {
        // Placeholder for actual EBICS statement download
        // For now, return mock data for demonstration
        return List.of(
            new StatementEntry(
                "TXN001",
                LocalDate.now().minusDays(1),
                LocalDate.now().minusDays(1),
                "1500.00",
                "EUR",
                "ACME Corp GmbH",
                "DE89370400440532013000",
                "Our Company Ltd",
                "DE12500105170648489890",
                "Invoice INV-2024-001 Payment",
                "END2END001"
            ),
            new StatementEntry(
                "TXN002",
                LocalDate.now().minusDays(2),
                LocalDate.now().minusDays(2),
                "2300.50",
                "EUR",
                "Client Services AG",
                "DE44500105175407324681",
                "Our Company Ltd", 
                "DE12500105170648489890",
                "Monthly service fee Q1",
                "END2END002"
            )
        );
    }
}
