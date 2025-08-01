package de.ferderer.ebicsdocusign.gateway.domain.workflows.model;

import java.time.LocalDateTime;

public record ContractCancelled(
    Long contractId,
    Long paymentId,
    String previousStatus,
    String cancellationReason,
    String docuSignResult,
    LocalDateTime cancelledAt
) {}
