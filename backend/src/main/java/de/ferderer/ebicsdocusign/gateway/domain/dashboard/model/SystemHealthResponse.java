package de.ferderer.ebicsdocusign.gateway.domain.dashboard.model;

import java.time.LocalDateTime;

public record SystemHealthResponse(
    String overallStatus,
    boolean databaseHealthy,
    boolean ebicsHealthy,
    long activeEbicsConnections,
    long totalEbicsConnections,
    LocalDateTime checkedAt
) {}
