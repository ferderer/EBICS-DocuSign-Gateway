package de.ferderer.ebicsdocusign.gateway.domain.docusign.model;

import java.time.LocalDateTime;

public record EnvelopeStatusInfo(
    Long id,
    String envelopeId,
    String templateName,
    String signerEmail,
    String signerName,
    String status,
    Long paymentId,
    LocalDateTime createdAt,
    LocalDateTime sentAt,
    LocalDateTime completedAt
) {}
