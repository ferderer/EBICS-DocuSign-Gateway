package de.ferderer.ebicsdocusign.gateway.domain.workflows.model;

import java.time.LocalDateTime;

public record ContractWorkflowInfo(
    Long id,
    Long paymentId,
    String transactionId,
    String status,
    String contractAmount,
    String contractCurrency,
    String clientName,
    String clientEmail,
    String projectDescription,
    String templateName,
    String envelopeId,
    LocalDateTime createdAt,
    LocalDateTime completedAt
) {}
