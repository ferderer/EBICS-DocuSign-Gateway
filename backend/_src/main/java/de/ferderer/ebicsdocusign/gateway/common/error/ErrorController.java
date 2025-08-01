package de.ferderer.ebicsdocusign.gateway.common.error;

import de.ferderer.ebicsdocusign.gateway.domain.Endpoints;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ErrorController {

    private final ErrorResponseBuilder errorResponseBuilder;

    @GetMapping(Endpoints.URL_ERROR)
    public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request) {
        Throwable error = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String path = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        
        if (path == null) {
            path = request.getRequestURI();
        }
        
        ErrorResponse response = errorResponseBuilder.buildErrorResponse(error, path);
        HttpStatus status = errorResponseBuilder.getStatus(error);
        
        return ResponseEntity.status(status).body(response);
    }
}
