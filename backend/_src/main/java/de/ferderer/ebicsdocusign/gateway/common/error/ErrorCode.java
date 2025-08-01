package de.ferderer.ebicsdocusign.gateway.common.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // AUTHENTICATION & AUTHORIZATION
    AUTH_ACCESS_DENIED(HttpStatus.FORBIDDEN, ErrorSeverity.HIGH, AlertLevel.MEDIUM, "Access denied to requested resource"),
    AUTH_INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, ErrorSeverity.HIGH, AlertLevel.HIGH, "JWT token is invalid or malformed"),
    AUTH_JWT_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, ErrorSeverity.MEDIUM, AlertLevel.LOW, "JWT token has expired"),
    AUTH_JWT_TOKEN_MALFORMED(HttpStatus.UNAUTHORIZED, ErrorSeverity.HIGH, AlertLevel.HIGH, "JWT token format is invalid"),
    AUTH_UNAUTHORIZED_WEBSOCKET_CONNECTION(HttpStatus.UNAUTHORIZED, ErrorSeverity.HIGH, AlertLevel.HIGH, "WebSocket connection not authorized"),
    AUTH_USER_SESSION_NOT_FOUND(HttpStatus.UNAUTHORIZED, ErrorSeverity.MEDIUM, AlertLevel.LOW, "User session not found or expired"),
    AUTH_SESSION_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, ErrorSeverity.MEDIUM, AlertLevel.MEDIUM, "Maximum concurrent sessions exceeded"),
    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, ErrorSeverity.MEDIUM, AlertLevel.LOW, "Invalid username or password"),
    AUTH_ACCOUNT_EXPIRED(HttpStatus.UNAUTHORIZED, ErrorSeverity.MEDIUM, AlertLevel.LOW, "User account has expired"),
    AUTH_ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED, ErrorSeverity.MEDIUM, AlertLevel.MEDIUM, "User account is locked"),
    AUTH_CREDENTIALS_EXPIRED(HttpStatus.UNAUTHORIZED, ErrorSeverity.MEDIUM, AlertLevel.LOW, "User credentials have expired"),
    AUTH_ACCOUNT_DISABLED(HttpStatus.UNAUTHORIZED, ErrorSeverity.MEDIUM, AlertLevel.MEDIUM, "User account is disabled"),
    AUTH_INSUFFICIENT_AUTHENTICATION(HttpStatus.UNAUTHORIZED, ErrorSeverity.MEDIUM, AlertLevel.LOW, "Insufficient authentication for requested resource"),
    AUTH_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, ErrorSeverity.MEDIUM, AlertLevel.LOW, "Authentication failed"),
    AUTH_SECURITY_VIOLATION(HttpStatus.FORBIDDEN, ErrorSeverity.HIGH, AlertLevel.HIGH, "Security violation detected"),

    // WEBSOCKET COMMUNICATION
    WS_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, ErrorSeverity.HIGH, AlertLevel.HIGH, "WebSocket connection establishment failed"),
    WS_CONNECTION_LOST(HttpStatus.SERVICE_UNAVAILABLE, ErrorSeverity.MEDIUM, AlertLevel.LOW, "WebSocket connection lost"),
    WS_MESSAGE_INVALID(HttpStatus.BAD_REQUEST, ErrorSeverity.LOW, AlertLevel.NONE, "Invalid WebSocket message format"),
    WS_SUBSCRIPTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, ErrorSeverity.MEDIUM, AlertLevel.MEDIUM, "WebSocket subscription failed"),
    WS_BROADCAST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, ErrorSeverity.HIGH, AlertLevel.HIGH, "Failed to broadcast message to clients"),
    WS_CLIENT_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, ErrorSeverity.LOW, AlertLevel.NONE, "WebSocket client connection timeout"),

    // INPUT VALIDATION & DATA
    VALIDATION_INVALID_INPUT(HttpStatus.BAD_REQUEST, ErrorSeverity.LOW, AlertLevel.NONE, "Input validation failed"),
    VALIDATION_MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, ErrorSeverity.LOW, AlertLevel.NONE, "Required field is missing"),
    VALIDATION_INVALID_FORMAT(HttpStatus.BAD_REQUEST, ErrorSeverity.LOW, AlertLevel.NONE, "Field format is invalid"),
    VALIDATION_VALUE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, ErrorSeverity.LOW, AlertLevel.NONE, "Field value is out of allowed range"),
    VALIDATION_METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, ErrorSeverity.LOW, AlertLevel.NONE, "HTTP method not allowed for this endpoint"),
    VALIDATION_INVALID_JSON(HttpStatus.BAD_REQUEST, ErrorSeverity.LOW, AlertLevel.NONE, "Invalid JSON format in request body"),
    VALIDATION_MISSING_PARAMETER(HttpStatus.BAD_REQUEST, ErrorSeverity.LOW, AlertLevel.NONE, "Required request parameter is missing"),
    VALIDATION_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, ErrorSeverity.LOW, AlertLevel.NONE, "Parameter type mismatch"),
    VALIDATION_BINDING_ERROR(HttpStatus.BAD_REQUEST, ErrorSeverity.LOW, AlertLevel.NONE, "Request binding failed"),
    VALIDATION_CONSTRAINT_VIOLATION(HttpStatus.BAD_REQUEST, ErrorSeverity.LOW, AlertLevel.NONE, "Constraint validation failed"),
    VALIDATION_INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, ErrorSeverity.LOW, AlertLevel.NONE, "Invalid argument provided"),
    
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorSeverity.LOW, AlertLevel.NONE, "Requested resource not found"),
    DATA_DUPLICATE_RESOURCE(HttpStatus.CONFLICT, ErrorSeverity.MEDIUM, AlertLevel.NONE, "Resource already exists"),
    DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, ErrorSeverity.HIGH, AlertLevel.HIGH, "Data integrity constraint violated"),
    DATA_OPTIMISTIC_LOCK_FAILURE(HttpStatus.CONFLICT, ErrorSeverity.MEDIUM, AlertLevel.LOW, "Resource was modified by another process"),
    DATA_PESSIMISTIC_LOCK_FAILURE(HttpStatus.CONFLICT, ErrorSeverity.MEDIUM, AlertLevel.MEDIUM, "Could not acquire database lock"),
    DATA_DEADLOCK_DETECTED(HttpStatus.CONFLICT, ErrorSeverity.HIGH, AlertLevel.HIGH, "Database deadlock detected"),
    DATA_LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, ErrorSeverity.MEDIUM, AlertLevel.MEDIUM, "Failed to acquire required lock"),
    DATA_INCONSISTENT_STATE(HttpStatus.CONFLICT, ErrorSeverity.HIGH, AlertLevel.HIGH, "Data in inconsistent state"),

    // BUSINESS LOGIC
    BUSINESS_RULE_VIOLATION(HttpStatus.UNPROCESSABLE_ENTITY, ErrorSeverity.MEDIUM, AlertLevel.NONE, "Business rule validation failed"),
    BUSINESS_OPERATION_NOT_ALLOWED(HttpStatus.FORBIDDEN, ErrorSeverity.MEDIUM, AlertLevel.NONE, "Operation not allowed in current state"),
    BUSINESS_INSUFFICIENT_PERMISSIONS(HttpStatus.FORBIDDEN, ErrorSeverity.MEDIUM, AlertLevel.NONE, "Insufficient permissions for operation"),
    BUSINESS_RESOURCE_LOCKED(HttpStatus.LOCKED, ErrorSeverity.MEDIUM, AlertLevel.NONE, "Resource is currently locked"),
    BUSINESS_INVALID_STATE(HttpStatus.CONFLICT, ErrorSeverity.MEDIUM, AlertLevel.LOW, "Invalid state for requested operation"),

    // EXTERNAL SERVICES
    EXTERNAL_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, ErrorSeverity.CRITICAL, AlertLevel.IMMEDIATE, "External service is unavailable"),
    EXTERNAL_SERVICE_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, ErrorSeverity.HIGH, AlertLevel.HIGH, "External service request timeout"),
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, ErrorSeverity.HIGH, AlertLevel.HIGH, "External API returned an error"),
    EXTERNAL_RATE_LIMITED(HttpStatus.TOO_MANY_REQUESTS, ErrorSeverity.MEDIUM, AlertLevel.MEDIUM, "Rate limited by external service"),

    // SYSTEM & INFRASTRUCTURE
    SYSTEM_DATABASE_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, ErrorSeverity.CRITICAL, AlertLevel.IMMEDIATE, "Database connection failed"),
    SYSTEM_CONFIGURATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorSeverity.CRITICAL, AlertLevel.IMMEDIATE, "System configuration error"),
    SYSTEM_MAINTENANCE_MODE(HttpStatus.SERVICE_UNAVAILABLE, ErrorSeverity.MEDIUM, AlertLevel.NONE, "System is in maintenance mode"),
    SYSTEM_FEATURE_DISABLED(HttpStatus.NOT_IMPLEMENTED, ErrorSeverity.LOW, AlertLevel.NONE, "Requested feature is disabled"),
    SYSTEM_DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorSeverity.HIGH, AlertLevel.HIGH, "Database operation failed"),
    SYSTEM_TRANSACTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, ErrorSeverity.HIGH, AlertLevel.HIGH, "Database transaction failed"),
    SYSTEM_DATABASE_TEMPORARY_FAILURE(HttpStatus.SERVICE_UNAVAILABLE, ErrorSeverity.MEDIUM, AlertLevel.MEDIUM, "Temporary database failure"),
    SYSTEM_IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorSeverity.HIGH, AlertLevel.HIGH, "Input/output operation failed"),

    // RATE LIMITING & THROTTLING
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, ErrorSeverity.MEDIUM, AlertLevel.MEDIUM, "Rate limit exceeded"),
    QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, ErrorSeverity.MEDIUM, AlertLevel.MEDIUM, "Quota limit exceeded"),
    CONCURRENT_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, ErrorSeverity.MEDIUM, AlertLevel.MEDIUM, "Concurrent request limit exceeded"),

    // GENERIC/FALLBACK
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorSeverity.CRITICAL, AlertLevel.IMMEDIATE, "Internal server error occurred"),
    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, ErrorSeverity.LOW, AlertLevel.NONE, "Feature not implemented"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, ErrorSeverity.HIGH, AlertLevel.HIGH, "Service temporarily unavailable"),

    // EBICS SPECIFIC
    EBICS_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, ErrorSeverity.CRITICAL, AlertLevel.IMMEDIATE, "EBICS connection failed"),
    EBICS_AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED, ErrorSeverity.HIGH, AlertLevel.HIGH, "EBICS authentication failed"),
    EBICS_PROTOCOL_ERROR(HttpStatus.BAD_GATEWAY, ErrorSeverity.HIGH, AlertLevel.HIGH, "EBICS protocol error"),
    EBICS_CERTIFICATE_INVALID(HttpStatus.UNAUTHORIZED, ErrorSeverity.HIGH, AlertLevel.HIGH, "EBICS certificate is invalid"),
    EBICS_SIGNATURE_VERIFICATION_FAILED(HttpStatus.UNAUTHORIZED, ErrorSeverity.HIGH, AlertLevel.HIGH, "EBICS signature verification failed"),
    EBICS_TRANSACTION_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, ErrorSeverity.HIGH, AlertLevel.HIGH, "EBICS transaction processing failed"),

    // DOCUSIGN SPECIFIC
    DOCUSIGN_ENVELOPE_CREATION_FAILED(HttpStatus.BAD_GATEWAY, ErrorSeverity.HIGH, AlertLevel.HIGH, "DocuSign envelope creation failed"),
    DOCUSIGN_SIGNING_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, ErrorSeverity.HIGH, AlertLevel.HIGH, "DocuSign signing process failed"),
    DOCUSIGN_DOCUMENT_PROCESSING_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, ErrorSeverity.MEDIUM, AlertLevel.HIGH, "Document processing error in DocuSign");

    public enum ErrorSeverity {
        CRITICAL,   // System unusable, immediate attention required
        HIGH,       // Major functionality impacted, urgent attention
        MEDIUM,     // Some functionality impacted, should be addressed
        LOW         // Minor issues, can be addressed in normal workflow
    }

    public enum AlertLevel {
        IMMEDIATE,     // Page/call on-call engineer immediately
        HIGH,          // Send to alert channel, escalate if not acknowledged
        MEDIUM,        // Log to monitoring, alert if threshold exceeded
        LOW,           // Log only, no active alerting
        NONE           // Silent, for expected business errors
    }

    public enum ErrorCategory {
        SECURITY,           // Authentication, authorization, access control
        COMMUNICATION,      // WebSocket, network communication
        DATA,              // Validation, database, data integrity
        BUSINESS,          // Business logic, domain rules
        EXTERNAL,          // Third-party services, APIs
        INTEGRATION,       // Specific integrations (EBICS, DocuSign)
        INFRASTRUCTURE,    // System, configuration, database
        THROTTLING,        // Rate limiting, quotas
        GENERAL           // Catch-all for uncategorized errors
    }

    private final HttpStatus defaultHttpStatus;
    private final ErrorSeverity severity;
    private final AlertLevel defaultAlertLevel;
    private final String defaultMessage;

    StandardErrorCode(HttpStatus status, ErrorSeverity severity, AlertLevel alertLevel, String message) {
        this.defaultHttpStatus = status;
        this.severity = severity;
        this.defaultAlertLevel = alertLevel;
        this.defaultMessage = message;
    }

    @Override
    public HttpStatus getDefaultHttpStatus() {
        return defaultHttpStatus;
    }

    @Override
    public ErrorSeverity getSeverity() {
        return severity;
    }

    @Override
    public AlertLevel getDefaultAlertLevel() {
        return defaultAlertLevel;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }
}
