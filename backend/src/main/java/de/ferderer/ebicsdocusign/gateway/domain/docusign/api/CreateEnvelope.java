package de.ferderer.ebicsdocusign.gateway.domain.docusign.api;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignEnvelopeEntity;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignEnvelopeEntity.EnvelopeStatus;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignEnvelopeRepository;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignTemplateEntity;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignTemplateRepository;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.EnvelopeCreated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreateEnvelope {
    
    private final DocuSignEnvelopeRepository envelopeRepository;
    private final DocuSignTemplateRepository templateRepository;
    
    public record CreateEnvelopeRequest(
        @NotNull Long templateId,
        @NotBlank @Email String signerEmail,
        @NotBlank String signerName,
        Map<String, String> templateFields,
        Long paymentId
    ) {}
    
    @PostMapping("/api/docusign/envelopes")
    public EnvelopeCreated createEnvelope(@Valid @RequestBody CreateEnvelopeRequest request) {
        var template = templateRepository.findById(request.templateId())
            .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND)
                .withData("templateId", request.templateId()));
        
        try {
            // TODO: Implement actual DocuSign envelope creation
            String envelopeId = performDocuSignEnvelopeCreation(request, template);
            
            // Save envelope to database
            var envelope = new DocuSignEnvelopeEntity();
            envelope.setId(System.currentTimeMillis()); // Temporary ID generation
            envelope.setEnvelopeId(envelopeId);
            envelope.setTemplateId(request.templateId());
            envelope.setSignerEmail(request.signerEmail());
            envelope.setSignerName(request.signerName());
            envelope.setStatus(EnvelopeStatus.SENT);
            envelope.setPaymentId(request.paymentId());
            envelope.setCreatedAt(LocalDateTime.now());
            
            envelopeRepository.save(envelope);
            
            return new EnvelopeCreated(
                envelope.getId(),
                envelopeId,
                template.getTemplateName(),
                request.signerEmail(),
                request.signerName(),
                "SENT",
                envelope.getCreatedAt()
            );
            
        }
        catch (Exception e) {
            throw new AppException(ErrorCode.DOCUSIGN_ENVELOPE_CREATION_FAILED)
                .withData("templateId", request.templateId())
                .withData("error", e.getMessage());
        }
    }
    
    private String performDocuSignEnvelopeCreation(CreateEnvelopeRequest request, DocuSignTemplateEntity template) {
        
        // Placeholder for actual DocuSign envelope creation
        // Generate mock envelope ID for now
        return "envelope-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
