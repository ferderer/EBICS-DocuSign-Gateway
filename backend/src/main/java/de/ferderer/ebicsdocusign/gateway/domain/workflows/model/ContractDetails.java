package de.ferderer.ebicsdocusign.gateway.domain.workflows.model;

import java.time.LocalDateTime;

public record ContractDetails(
    Long id,
    Long paymentId,
    String transactionId,
    String status,
    String contractAmount,
    String contractCurrency,
    String clientName,
    String clientEmail,
    String projectDescription,
    String contractReference,
    Long templateId,
    String templateName,
    Long envelopeId,
    String envelopeId_external,
    String signerEmail,
    String bankName,
    String hostId,
    String errorMessage,
    LocalDateTime createdAt,
    LocalDateTime processedAt,
    LocalDateTime sentAt,
    LocalDateTime completedAt
) {}
