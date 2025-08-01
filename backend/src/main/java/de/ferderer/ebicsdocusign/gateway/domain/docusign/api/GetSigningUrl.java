package de.ferderer.ebicsdocusign.gateway.domain.docusign.api;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignEnvelopeRepository;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.SigningUrlResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetSigningUrl {
    
    private final DocuSignEnvelopeRepository repository;
    
    @GetMapping("/api/docusign/envelopes/{envelopeId}/signing-url")
    public SigningUrlResponse getSigningUrl(
            @PathVariable String envelopeId,
            @RequestParam(defaultValue = "http://localhost:4200/signing/complete") String returnUrl) {
        
        var envelope = repository.findByEnvelopeId(envelopeId)
            .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND)
                .withData("envelopeId", envelopeId));
        
        try {
            // TODO: Implement actual DocuSign embedded signing URL generation
            String signingUrl = generateEmbeddedSigningUrl(envelopeId, envelope.getSignerEmail(), returnUrl);
            
            return new SigningUrlResponse(
                envelope.getId(),
                envelopeId,
                signingUrl,
                envelope.getSignerEmail(),
                envelope.getSignerName(),
                returnUrl,
                LocalDateTime.now().plusHours(1), // URL expires in 1 hour
                LocalDateTime.now()
            );
            
        } catch (Exception e) {
            throw new AppException(ErrorCode.DOCUSIGN_ENVELOPE_CREATION_FAILED)
                .withData("envelopeId", envelopeId)
                .withData("error", e.getMessage());
        }
    }
    
    private String generateEmbeddedSigningUrl(String envelopeId, String signerEmail, String returnUrl) {
        // Placeholder for actual DocuSign embedded signing URL generation
        // Generate mock signing URL for now
        return "https://demo.docusign.net/Member/PowerFormSigning.aspx?PowerFormId=" + 
               UUID.randomUUID().toString() + "&env=demo&acct=" + UUID.randomUUID().toString().substring(0, 8);
    }
}
