package de.ferderer.ebicsdocusign.gateway.common.error;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.stream.Collectors;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.AccountLockedException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {
    public record ViolatedConstraint(String code, String path, Object value) {
        public ViolatedConstraint(ConstraintViolation<?> v) {
            this(v.getMessage(), v.getPropertyPath().toString(), v.getInvalidValue());
        }

        public ViolatedConstraint(FieldError v) {
            this(v.getDefaultMessage(), v.getField(), v.getRejectedValue());
        }
    }

    public Map<String, Object> getErrorAttributes(Throwable ex, WebRequest webRequest) {
        webRequest.setAttribute("jakarta.servlet.error.exception", ex, WebRequest.SCOPE_REQUEST);

        var ea = super.getErrorAttributes(webRequest, ErrorAttributeOptions.of());
        ErrorCode code = mapException(ex);
        ea.put("code", code.name());
        ea.put("message", code.getMessage());
        ea.put("status", code.getHttpStatus());

        if (webRequest instanceof HttpServletRequest request) {
            String path = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

            if (path == null) {
                path = request.getRequestURI();
            }
            ea.put("path", path);
        }

        switch (ex) {
            case AppException e -> ea.put("data", e.getData());
            case ConstraintViolationException e -> ea.put("invalid", e.getConstraintViolations()
                .stream().map(ViolatedConstraint::new).collect(Collectors.toSet()));
            case MethodArgumentNotValidException e -> ea.put("invalid", e.getBindingResult().getFieldErrors()
                .stream().map(ViolatedConstraint::new).collect(Collectors.toSet()));
            case BindException e -> ea.put("invalid", e.getBindingResult().getFieldErrors()
                .stream().map(ViolatedConstraint::new).collect(Collectors.toSet()));
            default -> {}
        }

        return ea;
    }

    public HttpStatus getStatus(Throwable ex) {
        return mapException(ex).getHttpStatus();
    }

    public ErrorCode mapException(Throwable ex) {
        return switch(ex) {
            case AppException e -> e.getCode();

            // SPRING SECURITY EXCEPTIONS
            case AccessDeniedException e -> ErrorCode.AUTH_ACCESS_DENIED;
            case BadCredentialsException e -> ErrorCode.AUTH_INVALID_CREDENTIALS;
            case AccountExpiredException e -> ErrorCode.AUTH_ACCOUNT_EXPIRED;
            case AccountLockedException e -> ErrorCode.AUTH_ACCOUNT_LOCKED;
            case CredentialsExpiredException e -> ErrorCode.AUTH_CREDENTIALS_EXPIRED;
            case DisabledException e -> ErrorCode.AUTH_ACCOUNT_DISABLED;
            case InsufficientAuthenticationException e -> ErrorCode.AUTH_INSUFFICIENT_AUTHENTICATION;
            case AuthenticationException e -> ErrorCode.AUTH_AUTHENTICATION_FAILED;

            // SPRING WEB EXCEPTIONS
            case NoHandlerFoundException e -> ErrorCode.DATA_NOT_FOUND;
            case HttpRequestMethodNotSupportedException e -> ErrorCode.VALIDATION_METHOD_NOT_ALLOWED;
            case HttpMessageNotReadableException e -> ErrorCode.VALIDATION_INVALID_JSON;
            case MissingServletRequestParameterException e -> ErrorCode.VALIDATION_MISSING_PARAMETER;
            case MethodArgumentTypeMismatchException e -> ErrorCode.VALIDATION_TYPE_MISMATCH;
            case MethodArgumentNotValidException e -> ErrorCode.VALIDATION_INVALID_INPUT;
            case BindException e -> ErrorCode.VALIDATION_BINDING_ERROR;

            // VALIDATION EXCEPTIONS
            case ConstraintViolationException e -> ErrorCode.VALIDATION_CONSTRAINT_VIOLATION;

            // SPRING DATA/DAO EXCEPTIONS
            case DataAccessResourceFailureException e -> ErrorCode.SYSTEM_DATABASE_CONNECTION_FAILED;
            case DuplicateKeyException e -> ErrorCode.DATA_DUPLICATE_RESOURCE;
            case DataIntegrityViolationException e -> ErrorCode.DATA_INTEGRITY_VIOLATION;
            case EmptyResultDataAccessException e -> ErrorCode.DATA_NOT_FOUND;
            case IncorrectResultSizeDataAccessException e -> ErrorCode.DATA_INCONSISTENT_STATE;
            case ObjectOptimisticLockingFailureException e -> ErrorCode.DATA_OPTIMISTIC_LOCK_FAILURE;
            case OptimisticLockingFailureException e -> ErrorCode.DATA_OPTIMISTIC_LOCK_FAILURE;
            case CannotAcquireLockException e -> ErrorCode.DATA_LOCK_ACQUISITION_FAILED;
            case PessimisticLockingFailureException e -> ErrorCode.DATA_PESSIMISTIC_LOCK_FAILURE;
            case CannotCreateTransactionException e -> ErrorCode.SYSTEM_TRANSACTION_FAILED;
            case TransientDataAccessException e -> ErrorCode.SYSTEM_DATABASE_TEMPORARY_FAILURE;

            // JPA/HIBERNATE EXCEPTIONS
            case EntityNotFoundException e -> ErrorCode.DATA_NOT_FOUND;
            case OptimisticLockException e -> ErrorCode.DATA_OPTIMISTIC_LOCK_FAILURE;
            case JpaObjectRetrievalFailureException e -> ErrorCode.DATA_NOT_FOUND;
            case PersistenceException e -> ErrorCode.SYSTEM_DATABASE_ERROR;
        
            // COMMON JAVA EXCEPTIONS
            case IllegalArgumentException e -> ErrorCode.VALIDATION_INVALID_ARGUMENT;
            case IllegalStateException e -> ErrorCode.BUSINESS_INVALID_STATE;
            case UnsupportedOperationException e -> ErrorCode.NOT_IMPLEMENTED;
            case SecurityException e -> ErrorCode.AUTH_SECURITY_VIOLATION;
        
            // TIMEOUT AND CONNECTIVITY
            case SocketTimeoutException e -> ErrorCode.EXTERNAL_SERVICE_TIMEOUT;
            case ConnectException e -> ErrorCode.EXTERNAL_SERVICE_UNAVAILABLE;
            case IOException e -> ErrorCode.SYSTEM_IO_ERROR;

            default -> ErrorCode.INTERNAL_SERVER_ERROR;
        };
    }
}
