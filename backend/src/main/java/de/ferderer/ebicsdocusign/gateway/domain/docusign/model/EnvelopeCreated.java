package de.ferderer.ebicsdocusign.gateway.domain.docusign.model;

import java.time.LocalDateTime;

public record EnvelopeCreated(
    Long id,
    String envelopeId,
    String templateName,
    String signerEmail,
    String signerName,
    String status,
    LocalDateTime createdAt
) {}
