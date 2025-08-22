package de.ferderer.ebicsdocusign.gateway.common.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    
    private final ErrorResponseBuilder errorResponseBuilder;
    
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(AppException ex, HttpServletRequest request) {
        ErrorResponse response = errorResponseBuilder.buildErrorResponse(ex, request.getRequestURI());
        return ResponseEntity.status(ex.getStatus()).body(response);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        ErrorResponse response = errorResponseBuilder.buildErrorResponse(ex, request.getRequestURI());
        return ResponseEntity.status(errorResponseBuilder.getStatus(ex)).body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ErrorResponse response = errorResponseBuilder.buildErrorResponse(ex, request.getRequestURI());
        return ResponseEntity.status(errorResponseBuilder.getStatus(ex)).body(response);
    }
    
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(
            BindException ex, HttpServletRequest request) {
        ErrorResponse response = errorResponseBuilder.buildErrorResponse(ex, request.getRequestURI());
        return ResponseEntity.status(errorResponseBuilder.getStatus(ex)).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorResponse response = errorResponseBuilder.buildErrorResponse(ex, request.getRequestURI());
        return ResponseEntity.status(errorResponseBuilder.getStatus(ex)).body(response);
    }
}
