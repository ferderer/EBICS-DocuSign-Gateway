package de.ferderer.ebicsdocusign.gateway.domain.docusign.model;

import java.time.LocalDateTime;

public record WebhookResponse(
    String result,
    String message,
    String envelopeId,
    String currentStatus,
    LocalDateTime processedAt
) {}
