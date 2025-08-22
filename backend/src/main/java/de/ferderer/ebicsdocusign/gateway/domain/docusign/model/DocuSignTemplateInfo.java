package de.ferderer.ebicsdocusign.gateway.domain.docusign.model;

import java.time.LocalDateTime;

public record DocuSignTemplateInfo(
    Long id,
    String templateId,
    String templateName,
    String description,
    String status,
    String documentName,
    Integer pageCount,
    LocalDateTime createdAt,
    LocalDateTime lastUsed
) {}
