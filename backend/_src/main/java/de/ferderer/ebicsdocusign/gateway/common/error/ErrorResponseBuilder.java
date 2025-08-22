package de.ferderer.ebicsdocusign.gateway.common.error;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Component
public class ErrorResponseBuilder {
    
    private static final Map<Class<? extends Throwable>, HttpStatus> STATUS_MAP = new HashMap<>();
    
    static {
        STATUS_MAP.put(java.nio.file.AccessDeniedException.class, HttpStatus.FORBIDDEN);
//        STATUS_MAP.put(org.springframework.security.authentication.BadCredentialsException.class, HttpStatus.UNAUTHORIZED);
        STATUS_MAP.put(jakarta.validation.ConstraintViolationException.class, HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(org.springframework.http.converter.HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST);
//        STATUS_MAP.put(org.springframework.security.authentication.InsufficientAuthenticationException.class, HttpStatus.FORBIDDEN);
        STATUS_MAP.put(org.springframework.web.bind.MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(org.springframework.beans.TypeMismatchException.class, HttpStatus.BAD_REQUEST);
//        STATUS_MAP.put(org.springframework.security.core.userdetails.UsernameNotFoundException.class, HttpStatus.NOT_FOUND);
        STATUS_MAP.put(UnsupportedOperationException.class, HttpStatus.NOT_IMPLEMENTED);
    }

    public ErrorResponse buildErrorResponse(Throwable error, String path) {
        HttpStatus status = getStatus(error);
        ErrorResponse.Builder builder = ErrorResponse.builder()
            .status(status.value())
            .error(status.getReasonPhrase())
            .path(path);

        if (error != null) {
            // Set error code
            switch (error) {
                case AppException ex -> builder.code(ex.getCode().name());
                case ConstraintViolationException ex -> builder.code(StandardErrorCode.E_INVALID_INPUT.name());
                case MethodArgumentNotValidException ex -> builder.code(StandardErrorCode.E_INVALID_INPUT.name());
                case BindException ex -> builder.code(StandardErrorCode.E_INVALID_INPUT.name());
                default -> builder.code(error.getMessage());
            }

            // Set additional data
            switch (error) {
                case AppException ex -> builder.data(ex.getData());
                case ConstraintViolationException ex -> builder.invalid(ex.getConstraintViolations()
                    .stream().map(ErrorResponse.ViolatedConstraint::new).collect(Collectors.toSet()));
                case MethodArgumentNotValidException ex -> builder.invalid(ex.getBindingResult().getFieldErrors()
                    .stream().map(ErrorResponse.ViolatedConstraint::new).collect(Collectors.toSet()));
                case BindException ex -> builder.invalid(ex.getBindingResult().getFieldErrors()
                    .stream().map(ErrorResponse.ViolatedConstraint::new).collect(Collectors.toSet()));
                default -> {}
            }
        }

        return builder.build();
    }

    public HttpStatus getStatus(Throwable error) {
        if (error instanceof AppException exception) {
            return exception.getStatus();
        }
        else if (error != null && STATUS_MAP.containsKey(error.getClass())) {
            return STATUS_MAP.get(error.getClass());
        }
        else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
