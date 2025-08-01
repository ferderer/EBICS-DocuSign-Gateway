package de.ferderer.ebicsdocusign.gateway.domain.docusign.model;

import java.time.LocalDateTime;

public record SigningUrlResponse(
    Long id,
    String envelopeId,
    String signingUrl,
    String signerEmail,
    String signerName,
    String returnUrl,
    LocalDateTime expiresAt,
    LocalDateTime generatedAt
) {}

