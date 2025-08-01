package de.ferderer.ebicsdocusign.gateway.domain.workflows.model;

import java.time.LocalDateTime;

public record ContractStatusUpdated(
    Long contractId,
    Long paymentId,
    String previousStatus,
    String newStatus,
    String reason,
    LocalDateTime updatedAt
) {}
