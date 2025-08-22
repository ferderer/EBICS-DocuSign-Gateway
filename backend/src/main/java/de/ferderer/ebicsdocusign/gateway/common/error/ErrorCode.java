package de.ferderer.ebicsdocusign.gateway.common.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // AUTHENTICATION & AUTHORIZATION
    AUTH_ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access denied to requested resource"),
    AUTH_INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "JWT token is invalid or malformed"),
    AUTH_JWT_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT token has expired"),
    AUTH_JWT_TOKEN_MALFORMED(HttpStatus.UNAUTHORIZED, "JWT token format is invalid"),
    AUTH_UNAUTHORIZED_WEBSOCKET_CONNECTION(HttpStatus.UNAUTHORIZED, "WebSocket connection not authorized"),
    AUTH_USER_SESSION_NOT_FOUND(HttpStatus.UNAUTHORIZED, "User session not found or expired"),
    AUTH_SESSION_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Maximum concurrent sessions exceeded"),
    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid username or password"),
    AUTH_ACCOUNT_EXPIRED(HttpStatus.UNAUTHORIZED, "User account has expired"),
    AUTH_ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED, "User account is locked"),
    AUTH_CREDENTIALS_EXPIRED(HttpStatus.UNAUTHORIZED, "User credentials have expired"),
    AUTH_ACCOUNT_DISABLED(HttpStatus.UNAUTHORIZED, "User account is disabled"),
    AUTH_INSUFFICIENT_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "Insufficient authentication for requested resource"),
    AUTH_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "Authentication failed"),
    AUTH_SECURITY_VIOLATION(HttpStatus.FORBIDDEN, "Security violation detected"),

    // WEBSOCKET COMMUNICATION
    WS_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "WebSocket connection establishment failed"),
    WS_CONNECTION_LOST(HttpStatus.SERVICE_UNAVAILABLE, "WebSocket connection lost"),
    WS_MESSAGE_INVALID(HttpStatus.BAD_REQUEST, "Invalid WebSocket message format"),
    WS_SUBSCRIPTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WebSocket subscription failed"),
    WS_BROADCAST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to broadcast message to clients"),
    WS_CLIENT_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "WebSocket client connection timeout"),

    // INPUT VALIDATION & DATA
    VALIDATION_INVALID_INPUT(HttpStatus.BAD_REQUEST, "Input validation failed"),
    VALIDATION_MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "Required field is missing"),
    VALIDATION_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "Field format is invalid"),
    VALIDATION_VALUE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "Field value is out of allowed range"),
    VALIDATION_METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not allowed for this endpoint"),
    VALIDATION_INVALID_JSON(HttpStatus.BAD_REQUEST, "Invalid JSON format in request body"),
    VALIDATION_MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "Required request parameter is missing"),
    VALIDATION_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "Parameter type mismatch"),
    VALIDATION_BINDING_ERROR(HttpStatus.BAD_REQUEST, "Request binding failed"),
    VALIDATION_CONSTRAINT_VIOLATION(HttpStatus.BAD_REQUEST, "Constraint validation failed"),
    VALIDATION_INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "Invalid argument provided"),
    
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "Requested resource not found"),
    DATA_DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "Resource already exists"),
    DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, "Data integrity constraint violated"),
    DATA_OPTIMISTIC_LOCK_FAILURE(HttpStatus.CONFLICT, "Resource was modified by another process"),
    DATA_PESSIMISTIC_LOCK_FAILURE(HttpStatus.CONFLICT, "Could not acquire database lock"),
    DATA_DEADLOCK_DETECTED(HttpStatus.CONFLICT, "Database deadlock detected"),
    DATA_LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, "Failed to acquire required lock"),
    DATA_INCONSISTENT_STATE(HttpStatus.CONFLICT, "Data in inconsistent state"),

    // BUSINESS LOGIC
    BUSINESS_RULE_VIOLATION(HttpStatus.UNPROCESSABLE_ENTITY, "Business rule validation failed"),
    BUSINESS_OPERATION_NOT_ALLOWED(HttpStatus.FORBIDDEN, "Operation not allowed in current state"),
    BUSINESS_INSUFFICIENT_PERMISSIONS(HttpStatus.FORBIDDEN, "Insufficient permissions for operation"),
    BUSINESS_RESOURCE_LOCKED(HttpStatus.LOCKED, "Resource is currently locked"),
    BUSINESS_INVALID_STATE(HttpStatus.CONFLICT, "Invalid state for requested operation"),

    // EXTERNAL SERVICES
    EXTERNAL_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "External service is unavailable"),
    EXTERNAL_SERVICE_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "External service request timeout"),
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "External API returned an error"),
    EXTERNAL_RATE_LIMITED(HttpStatus.TOO_MANY_REQUESTS, "Rate limited by external service"),

    // SYSTEM & INFRASTRUCTURE
    SYSTEM_DATABASE_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "Database connection failed"),
    SYSTEM_CONFIGURATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "System configuration error"),
    SYSTEM_MAINTENANCE_MODE(HttpStatus.SERVICE_UNAVAILABLE, "System is in maintenance mode"),
    SYSTEM_FEATURE_DISABLED(HttpStatus.NOT_IMPLEMENTED, "Requested feature is disabled"),
    SYSTEM_DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Database operation failed"),
    SYSTEM_TRANSACTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Database transaction failed"),
    SYSTEM_DATABASE_TEMPORARY_FAILURE(HttpStatus.SERVICE_UNAVAILABLE, "Temporary database failure"),
    SYSTEM_IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Input/output operation failed"),

    // RATE LIMITING & THROTTLING
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"),
    QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Quota limit exceeded"),
    CONCURRENT_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Concurrent request limit exceeded"),

    // GENERIC/FALLBACK
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error occurred"),
    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "Feature not implemented"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Service temporarily unavailable"),

    // EBICS SPECIFIC
    EBICS_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "EBICS connection failed"),
    EBICS_AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED, "EBICS authentication failed"),
    EBICS_PROTOCOL_ERROR(HttpStatus.BAD_GATEWAY, "EBICS protocol error"),
    EBICS_CERTIFICATE_INVALID(HttpStatus.UNAUTHORIZED, "EBICS certificate is invalid"),
    EBICS_SIGNATURE_VERIFICATION_FAILED(HttpStatus.UNAUTHORIZED, "EBICS signature verification failed"),
    EBICS_TRANSACTION_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, "EBICS transaction processing failed"),
    EBICS_PROCESSING_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "EBICS processing failed"),

    // DOCUSIGN SPECIFIC
    DOCUSIGN_ENVELOPE_CREATION_FAILED(HttpStatus.BAD_GATEWAY, "DocuSign envelope creation failed"),
    DOCUSIGN_SIGNING_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, "DocuSign signing process failed"),
    DOCUSIGN_DOCUMENT_PROCESSING_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "Document processing error in DocuSign");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
