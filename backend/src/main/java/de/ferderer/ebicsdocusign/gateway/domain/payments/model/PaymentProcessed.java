package de.ferderer.ebicsdocusign.gateway.domain.payments.model;

import java.time.LocalDateTime;

public record PaymentProcessed(
    Long paymentId,
    String transactionId,
    String amount,
    String currency,
    String debtorName,
    Long workflowId,
    String workflowStatus,
    String templateName,
    String clientEmail,
    LocalDateTime processedAt
) {}
