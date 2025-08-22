package de.ferderer.ebicsdocusign.gateway.domain.docusign.api;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignEnvelopeRepository;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.EnvelopeStatusInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetEnvelopeStatus {
    
    private final DocuSignEnvelopeRepository repository;
    
    @GetMapping("/api/docusign/envelopes/{envelopeId}/status")
    public EnvelopeStatusInfo getEnvelopeStatus(@PathVariable String envelopeId) {
        return repository.findEnvelopeStatus(envelopeId)
            .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND)
                .withData("envelopeId", envelopeId));
    }
}
