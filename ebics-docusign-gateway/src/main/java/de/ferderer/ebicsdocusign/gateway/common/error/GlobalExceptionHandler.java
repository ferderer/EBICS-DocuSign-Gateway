package de.ferderer.ebicsdocusign.gateway.common.error;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    
    private final CustomErrorAttributes ea;
    
    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, Object>> handleAppException(AppException ex, WebRequest request) {
        return ResponseEntity.status(ex.getCode().getHttpStatus()).body(ea.getErrorAttributes(ex, request));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Throwable ex, WebRequest request) {
        return ResponseEntity.status(ea.getStatus(ex)).body(ea.getErrorAttributes(ex, request));
    }
}
