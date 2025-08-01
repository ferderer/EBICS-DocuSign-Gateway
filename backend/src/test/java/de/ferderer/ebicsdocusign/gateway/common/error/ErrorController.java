package de.ferderer.ebicsdocusign.gateway.common.error;

import de.ferderer.ebicsdocusign.gateway.common.error.CustomErrorAttributes;
import de.ferderer.ebicsdocusign.gateway.domain.Endpoints;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequiredArgsConstructor
public class ErrorController {

    private final CustomErrorAttributes ea;

    @GetMapping(Endpoints.URL_ERROR)
    public Map<String, Object> handleError(WebRequest request) {
        return ea.getErrorAttributes(request, ErrorAttributeOptions.of());
    }
}
