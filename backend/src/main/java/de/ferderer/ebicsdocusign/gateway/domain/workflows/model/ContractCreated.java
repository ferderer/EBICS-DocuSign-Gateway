package de.ferderer.ebicsdocusign.gateway.domain.workflows.model;

import java.time.LocalDateTime;

public record ContractCreated(
    Long id,
    Long paymentId,
    String transactionId,
    String status,
    String contractAmount,
    String contractCurrency,
    String clientName,
    String clientEmail,
    LocalDateTime createdAt
) {}
