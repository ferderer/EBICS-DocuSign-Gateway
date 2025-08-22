package de.ferderer.ebicsdocusign.gateway.common.error;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Maps Spring Framework and JPA exceptions to our application ErrorCodes.
 * This provides consistent error handling across all framework-level exceptions.
 */
public class ExceptionMapper {

    public static StandardErrorCode mapException(Throwable exception) {
        return switch(exception) {
            case NoHandlerFoundException e -> StandardErrorCode.DATA_NOT_FOUND;
            default -> null;
        };
    } 
    
    private static final Map<Class<? extends Throwable>, StandardErrorCode> MAPPINGS = new HashMap<>();
    
    static {
        /*/ SPRING SECURITY EXCEPTIONS
        EXCEPTION_MAPPINGS.put(AccessDeniedException.class, ErrorCode.AUTH_ACCESS_DENIED);
        EXCEPTION_MAPPINGS.put(BadCredentialsException.class, ErrorCode.AUTH_INVALID_CREDENTIALS);
        EXCEPTION_MAPPINGS.put(AccountExpiredException.class, ErrorCode.AUTH_ACCOUNT_EXPIRED);
        EXCEPTION_MAPPINGS.put(AccountLockedException.class, ErrorCode.AUTH_ACCOUNT_LOCKED);
        EXCEPTION_MAPPINGS.put(CredentialsExpiredException.class, ErrorCode.AUTH_CREDENTIALS_EXPIRED);
        EXCEPTION_MAPPINGS.put(DisabledException.class, ErrorCode.AUTH_ACCOUNT_DISABLED);
        EXCEPTION_MAPPINGS.put(InsufficientAuthenticationException.class, ErrorCode.AUTH_INSUFFICIENT_AUTHENTICATION);
        EXCEPTION_MAPPINGS.put(AuthenticationException.class, ErrorCode.AUTH_AUTHENTICATION_FAILED); */
        
        // ========================================
        // SPRING WEB EXCEPTIONS
        // ========================================
        EXCEPTION_MAPPINGS.put(NoHandlerFoundException.class, ErrorCode.DATA_NOT_FOUND);
        EXCEPTION_MAPPINGS.put(HttpRequestMethodNotSupportedException.class, ErrorCode.VALIDATION_METHOD_NOT_ALLOWED);
        EXCEPTION_MAPPINGS.put(HttpMessageNotReadableException.class, ErrorCode.VALIDATION_INVALID_JSON);
        EXCEPTION_MAPPINGS.put(MissingServletRequestParameterException.class, ErrorCode.VALIDATION_MISSING_PARAMETER);
        EXCEPTION_MAPPINGS.put(MethodArgumentTypeMismatchException.class, ErrorCode.VALIDATION_TYPE_MISMATCH);
        EXCEPTION_MAPPINGS.put(MethodArgumentNotValidException.class, ErrorCode.VALIDATION_INVALID_INPUT);
        EXCEPTION_MAPPINGS.put(BindException.class, ErrorCode.VALIDATION_BINDING_ERROR);
        
        // ========================================
        // VALIDATION EXCEPTIONS
        // ========================================
        EXCEPTION_MAPPINGS.put(ConstraintViolationException.class, ErrorCode.VALIDATION_CONSTRAINT_VIOLATION);
        
        // ========================================
        // SPRING DATA/DAO EXCEPTIONS
        // ========================================
        EXCEPTION_MAPPINGS.put(DataAccessResourceFailureException.class, ErrorCode.SYSTEM_DATABASE_CONNECTION_FAILED);
        EXCEPTION_MAPPINGS.put(DataIntegrityViolationException.class, ErrorCode.DATA_INTEGRITY_VIOLATION);
        EXCEPTION_MAPPINGS.put(DuplicateKeyException.class, ErrorCode.DATA_DUPLICATE_RESOURCE);
        EXCEPTION_MAPPINGS.put(EmptyResultDataAccessException.class, ErrorCode.DATA_NOT_FOUND);
        EXCEPTION_MAPPINGS.put(IncorrectResultSizeDataAccessException.class, ErrorCode.DATA_INCONSISTENT_STATE);
        EXCEPTION_MAPPINGS.put(OptimisticLockingFailureException.class, ErrorCode.DATA_OPTIMISTIC_LOCK_FAILURE);
        EXCEPTION_MAPPINGS.put(PessimisticLockingFailureException.class, ErrorCode.DATA_PESSIMISTIC_LOCK_FAILURE);
        EXCEPTION_MAPPINGS.put(DeadlockLoserDataAccessException.class, ErrorCode.DATA_DEADLOCK_DETECTED);
        EXCEPTION_MAPPINGS.put(CannotAcquireLockException.class, ErrorCode.DATA_LOCK_ACQUISITION_FAILED);
        EXCEPTION_MAPPINGS.put(CannotCreateTransactionException.class, ErrorCode.SYSTEM_TRANSACTION_FAILED);
        EXCEPTION_MAPPINGS.put(TransientDataAccessException.class, ErrorCode.SYSTEM_DATABASE_TEMPORARY_FAILURE);

        // JPA/HIBERNATE EXCEPTIONS
        EXCEPTION_MAPPINGS.put(EntityNotFoundException.class, ErrorCode.DATA_NOT_FOUND);
        EXCEPTION_MAPPINGS.put(OptimisticLockException.class, ErrorCode.DATA_OPTIMISTIC_LOCK_FAILURE);
        EXCEPTION_MAPPINGS.put(JpaObjectRetrievalFailureException.class, ErrorCode.DATA_NOT_FOUND);
        EXCEPTION_MAPPINGS.put(ObjectOptimisticLockingFailureException.class, ErrorCode.DATA_OPTIMISTIC_LOCK_FAILURE);
        EXCEPTION_MAPPINGS.put(PersistenceException.class, ErrorCode.SYSTEM_DATABASE_ERROR);
        
        // COMMON JAVA EXCEPTIONS
        EXCEPTION_MAPPINGS.put(IllegalArgumentException.class, ErrorCode.VALIDATION_INVALID_ARGUMENT);
        EXCEPTION_MAPPINGS.put(IllegalStateException.class, ErrorCode.BUSINESS_INVALID_STATE);
        EXCEPTION_MAPPINGS.put(UnsupportedOperationException.class, ErrorCode.NOT_IMPLEMENTED);
        EXCEPTION_MAPPINGS.put(SecurityException.class, ErrorCode.AUTH_SECURITY_VIOLATION);
        
        // TIMEOUT AND CONNECTIVITY
        EXCEPTION_MAPPINGS.put(java.net.SocketTimeoutException.class, ErrorCode.EXTERNAL_SERVICE_TIMEOUT);
        EXCEPTION_MAPPINGS.put(java.net.ConnectException.class, ErrorCode.EXTERNAL_SERVICE_UNAVAILABLE);
        EXCEPTION_MAPPINGS.put(java.io.IOException.class, ErrorCode.SYSTEM_IO_ERROR);
    }
    
    /**
     * Maps a Spring/framework exception to our application ErrorCode.
     * Uses inheritance hierarchy to find the best match.
     */
    public static Optional<ErrorCode> mapException(Throwable exception) {
        if (exception == null) {
            return Optional.empty();
        }
        
        // Direct mapping first
        ErrorCode directMapping = EXCEPTION_MAPPINGS.get(exception.getClass());
        if (directMapping != null) {
            return Optional.of(directMapping);
        }
        
        // Walk up the inheritance hierarchy
        Class<?> currentClass = exception.getClass();
        while (currentClass != null && currentClass != Object.class) {
            ErrorCode mapping = EXCEPTION_MAPPINGS.get(currentClass);
            if (mapping != null) {
                return Optional.of(mapping);
            }
            currentClass = currentClass.getSuperclass();
        }
        
        // Check implemented interfaces
        for (Class<?> interfaceClass : exception.getClass().getInterfaces()) {
            ErrorCode mapping = EXCEPTION_MAPPINGS.get(interfaceClass);
            if (mapping != null) {
                return Optional.of(mapping);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Maps exception with fallback to a default error code if no mapping found.
     */
    public static ErrorCode mapExceptionWithFallback(Throwable exception, ErrorCode fallback) {
        return mapException(exception).orElse(fallback);
    }
    
    /**
     * Maps exception with intelligent fallback based on exception type.
     */
    public static ErrorCode mapExceptionWithIntelligentFallback(Throwable exception) {
        Optional<ErrorCode> mapped = mapException(exception);
        if (mapped.isPresent()) {
            return mapped.get();
        }
        
        // Intelligent fallback based on exception package/type
        String exceptionClass = exception.getClass().getName();
        
        if (exceptionClass.contains("security") || exceptionClass.contains("auth")) {
            return ErrorCode.AUTH_AUTHENTICATION_FAILED;
        }
        if (exceptionClass.contains("validation") || exceptionClass.contains("bind")) {
            return ErrorCode.VALIDATION_INVALID_INPUT;
        }
        if (exceptionClass.contains("data") || exceptionClass.contains("sql") || exceptionClass.contains("hibernate")) {
            return ErrorCode.SYSTEM_DATABASE_ERROR;
        }
        if (exceptionClass.contains("timeout") || exceptionClass.contains("connect")) {
            return ErrorCode.EXTERNAL_SERVICE_TIMEOUT;
        }
        
        return ErrorCode.INTERNAL_SERVER_ERROR;
    }
    
    /**
     * Check if an exception type has a specific mapping.
     */
    public static boolean hasMappingFor(Class<? extends Throwable> exceptionClass) {
        return EXCEPTION_MAPPINGS.containsKey(exceptionClass);
    }
    
    /**
     * Get all mapped exception types (useful for documentation/testing).
     */
    public static Map<Class<? extends Throwable>, ErrorCode> getAllMappings() {
        return new HashMap<>(EXCEPTION_MAPPINGS);
    }
}
