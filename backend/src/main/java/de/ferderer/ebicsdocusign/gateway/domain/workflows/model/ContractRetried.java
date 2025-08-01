package de.ferderer.ebicsdocusign.gateway.domain.workflows.model;

import java.time.LocalDateTime;

public record ContractRetried(
    Long contractId,
    Long paymentId,
    String previousStatus,
    String newStatus,
    String previousError,
    String retryReason,
    LocalDateTime retriedAt
) {}
